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
import com.threelab.apsensi.Helper.ImageUploader
import com.threelab.apsensi.Helper.PreferencesHelper
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
    private var photoFile: File? = null

    private lateinit var requestQueue: RequestQueue
    private lateinit var photoText: TextView
    private lateinit var attRecycle: RecyclerView
    private lateinit var sharedPref: PreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_absen, container, false)
        val cameraScan: Button = view.findViewById(R.id.cameraScan)

        photoText = view.findViewById(R.id.photoText)
        attRecycle = view.findViewById(R.id.attendanceRecycle)
        requestQueue = Volley.newRequestQueue(requireContext())
        sharedPref = PreferencesHelper(requireContext())

        cameraScan.setOnClickListener {
            capturePhoto()
        }

        initializedOpenCamera()
        fillAttendanceLogs()

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val endpoint = Constant.API_ENDPOINT + "/attendances/attempt"
        val authToken = sharedPref.getString(Constant.PREF_TOKEN) ?: ""

        Log.d("Anjay", endpoint)


        if (resultCode == Activity.RESULT_OK && requestCode == 200 && data != null) {
            urlImage = data.extras?.get("data") as Bitmap?

            ImageUploader(requireContext()).uploadImage(
                endpoint,
                authToken,
                bitmapToByteArray(urlImage),
                {response ->
                    Log.d("Anjay", response.toString())
                },
                {error ->
                    Log.d("Anjay", String(error.networkResponse.data))
                }
            )
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
        val headers = HashMap<String, String>()

        headers["Authorization"] = sharedPref.getString(Constant.PREF_TOKEN).toString()

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
                return headers
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
