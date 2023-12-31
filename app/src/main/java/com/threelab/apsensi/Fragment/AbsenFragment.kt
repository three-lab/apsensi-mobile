package com.threelab.apsensi.Fragment

import android.Manifest
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
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.threelab.apsensi.Helper.Constant
import com.threelab.apsensi.Helper.FileHelper
import com.threelab.apsensi.Helper.FileUploader
import com.threelab.apsensi.Helper.PreferencesHelper
import com.threelab.apsensi.LoadingDialog
import com.threelab.apsensi.PresenceFailedActivity
import com.threelab.apsensi.PresenceSuccess
import com.threelab.apsensi.R
import com.threelab.apsensi.adapters.AttendanceAdapter
import com.threelab.apsensi.data.AttendanceItem
import org.json.JSONObject
import java.io.ByteArrayOutputStream


class AbsenFragment  : Fragment() {
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

    private fun capturePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        imageActivityResultLauncher.launch(cameraIntent)
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
                Log.d("Presensi", error.networkResponse.toString())
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
                Log.e("Presensi", error.networkResponse.toString())
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
                Log.e("Presensi", String(error.networkResponse.data))
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

    val imageActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if(result.resultCode == AppCompatActivity.RESULT_OK) {
                loadingDialog.showLoading()

                val bitmap: Bitmap? = result.data?.extras?.get("data") as Bitmap?
                val endpoint = Constant.API_ENDPOINT + "/attendances/attempt"
                val authToken = sharedPref.getString(Constant.PREF_TOKEN) ?: ""

                FileUploader(requireContext()).uploadFile(
                    endpoint,
                    authToken,
                    FileHelper().bitmapToByteArray(bitmap),
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


                        intent.putExtra("message", message)
                        loadingDialog.hideLoading()
                        startActivity(intent)
                    }
                )
            }
        }
    )
}
