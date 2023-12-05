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
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.threelab.apsensi.Helper.Constant
import com.threelab.apsensi.Helper.ImageUploader
import com.threelab.apsensi.Helper.PreferencesHelper
import com.threelab.apsensi.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AbsenFragment  : Fragment() {
    private var urlImage: Bitmap? = null
    private var photoFile: File? = null

    private lateinit var requestQueue: RequestQueue
    private lateinit var photoText: TextView
    private lateinit var sharedPref: PreferencesHelper
    private lateinit var listItemPresensiAdapter: ListItemPresensi


    public val daftarPresensi: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_absen, container, false)

        val cameraScan: Button = view.findViewById(R.id.cameraScan)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val listItemPresensiAdapter = ListItemPresensi()
        recyclerView.adapter = listItemPresensiAdapter

        listItemPresensiAdapter.daftarPresensi.addAll(listOf("item 1"))

        listItemPresensiAdapter.notifyDataSetChanged()

        photoText = view.findViewById(R.id.photoText)
        requestQueue = Volley.newRequestQueue(requireContext())
        sharedPref = PreferencesHelper(requireContext())

        cameraScan.setOnClickListener {
            capturePhoto()
        }

        initializedOpenCamera()

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val endpoint = Constant.API_ENDPOINT + "/attendances/attempt"
        val authToken = sharedPref.getString(Constant.PREF_TOKEN) ?: ""


        if (resultCode == Activity.RESULT_OK && requestCode == 200 && data != null) {
            urlImage = data.extras?.get("data") as Bitmap?

            ImageUploader(requireContext()).uploadImage(
                endpoint,
                authToken,
                bitmapToByteArray(urlImage),
                {response ->

                },
                {error ->
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        Log.e("Error", String(error.networkResponse.data))
                    } else {
                        Log.e("Error", "Error response is null or doesn't contain data.")
                    }

                }
            )
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap?): File {
        val file = File.createTempFile("temp_image", null, requireActivity().cacheDir)

        try {
            val outputStream = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file
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
