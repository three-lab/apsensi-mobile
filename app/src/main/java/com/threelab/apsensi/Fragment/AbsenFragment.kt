package com.threelab.apsensi.Fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.threelab.apsensi.Helper.Constant
import com.threelab.apsensi.Helper.FileUploader
import com.threelab.apsensi.Helper.ImageUploader
import com.threelab.apsensi.Helper.PreferencesHelper
import com.threelab.apsensi.LoadingDialog
import com.threelab.apsensi.PresenceFailedActivity
import com.threelab.apsensi.PresenceSuccess
import com.threelab.apsensi.R
import com.threelab.apsensi.adapters.AttendanceAdapter
import com.threelab.apsensi.data.AttendanceItem
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AbsenFragment  : Fragment() {
    private var urlImage: Bitmap? = null

    private lateinit var requestQueue: RequestQueue
    private lateinit var photoText: TextView
    private lateinit var attRecycle: RecyclerView
    private lateinit var sharedPref: PreferencesHelper
    private lateinit var startPresenceBtn: Button
    private lateinit var endPresenceBtn: Button
    private lateinit var cameraScan: Button
    private lateinit var reqHeaders: MutableMap<String, String>
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_absen, container, false)

        loadingDialog = LoadingDialog(requireContext())
        sharedPref = PreferencesHelper(requireContext())
        cameraScan = view.findViewById(R.id.cameraScan)
        startPresenceBtn = view.findViewById(R.id.startPresence)
        endPresenceBtn = view.findViewById(R.id.endPresence)
        photoText = view.findViewById(R.id.photoText)
        attRecycle = view.findViewById(R.id.attendanceRecycle)
        requestQueue = Volley.newRequestQueue(requireContext())
        reqHeaders = HashMap<String, String>()

        reqHeaders["Authorization"] = sharedPref.getString(Constant.PREF_TOKEN).toString()

        cameraScan.setOnClickListener {
            capturePhoto()
        }

        startPresenceBtn.setOnClickListener {managePresence(
            "/attendances/attempt",
            null
        )}

        endPresenceBtn.setOnClickListener {managePresence(
            "/attendances/finish-attempt",
            "Berhasil mengakhiri presensi, selamat melanjutkan aktivitas anda"
        )}

        initializedOpenCamera()
        fillAttendanceLogs()
        manageButton()

        loadingDialog.showLoading()

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val endpoint = Constant.API_ENDPOINT + "/attendances/attempt"
        val authToken = sharedPref.getString(Constant.PREF_TOKEN) ?: ""


        if (resultCode == Activity.RESULT_OK && requestCode == 200 && data != null) {
            urlImage = data.extras?.get("data") as Bitmap?
            loadingDialog.showLoading()

            FileUploader(requireContext()).uploadFile(
                endpoint,
                authToken,
                bitmapToByteArray(urlImage),
                "image",
                "image.jpg",
                "image/jpeg",
                {response ->
                    val intent = Intent(requireContext(), PresenceSuccess::class.java)

                    loadingDialog.hideLoading()
                    startActivity(intent)
                },
                {error ->
                    val response = JSONObject(String(error.networkResponse?.data ?: ByteArray(0)))
                    val message = response.getJSONObject("meta").getString("message")
                    val intent = Intent(requireContext(), PresenceFailedActivity::class.java)

                    Log.e("Presensi", String(error.networkResponse.data))

                    intent.putExtra("message", message)
                    loadingDialog.hideLoading()
                    startActivity(intent)
                }
            )

//            ImageUploader(requireContext()).uploadImage(
//                endpoint,
//                authToken,
//                bitmapToByteArray(urlImage),
//                {response ->
//                    val intent = Intent(requireContext(), PresenceSuccess::class.java)
//
//                    loadingDialog.hideLoading()
//                    startActivity(intent)
//                },
//                {error ->
//                    val response = JSONObject(String(error.networkResponse?.data ?: ByteArray(0)))
//                    val message = response.getJSONObject("meta").getString("message")
//                    val intent = Intent(requireContext(), PresenceFailedActivity::class.java)
//
//                    intent.putExtra("message", message)
//                    loadingDialog.hideLoading()
//                    startActivity(intent)
//                }
//            )
        }
    }

    fun bitmapToByteArray(bitmap: Bitmap?): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    private fun capturePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 200)
    }

    private fun fillAttendanceLogs() {
        val attUrl = Constant.API_ENDPOINT + "/attendances/logs"

        val request = object: JsonObjectRequest(Request.Method.GET, attUrl, null,
            {response ->
                val gson = Gson()
                val attendanceType = object: TypeToken<List<AttendanceItem>>() {}.type
                val attendanceItems: List<AttendanceItem> = gson.fromJson(
                    response.getJSONArray("data").toString(),
                    attendanceType
                )

                val adapter = AttendanceAdapter(attendanceItems)
                attRecycle.adapter = adapter
            }, {error ->
                Log.d("Anjay", error.networkResponse.toString())
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                return reqHeaders
            }
        }

        requestQueue.add(request)
    }

    private fun manageButton() {
        val url = Constant.API_ENDPOINT + "/attendances/status"

        val request = object: JsonObjectRequest(Request.Method.GET, url, null,
            {response ->
                val data = response.getJSONObject("data")
                val isStarted: Boolean = data.getBoolean("started")
                val isScanned: Boolean = data.getBoolean("scanned")
                val canAttempt: Boolean = data.getBoolean("canAttempt")

                if(isStarted) {
                    endPresenceBtn.visibility = View.VISIBLE
                } else if(isScanned) {
                    startPresenceBtn.visibility = View.VISIBLE
                    startPresenceBtn.isEnabled = canAttempt
                } else {
                    cameraScan.visibility = View.VISIBLE
                    cameraScan.isEnabled = canAttempt
                }

                loadingDialog.hideLoading()
            }, {error ->
                Log.e("Anjay", error.networkResponse.toString())
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                return reqHeaders
            }
        }

        requestQueue.add(request)
    }

    private fun managePresence(endpoint: String, message: String?) {
        val url = Constant.API_ENDPOINT + endpoint
        loadingDialog.showLoading()

        val request = object: JsonObjectRequest(Request.Method.POST, url, null,
            {response ->
                val intent = Intent(requireContext(), PresenceSuccess::class.java)
                intent.putExtra("message", message)

                startActivity(intent)
            }, {error ->
                Log.e("Anjay", String(error.networkResponse.data))
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                return reqHeaders
            }
        }

        requestQueue.add(request)
    }

    private fun initializedOpenCamera() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), Manifest.permission.CAMERA
                )
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(Manifest.permission.CAMERA), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(Manifest.permission.CAMERA), 1
                )
            }
        }
    }
}
