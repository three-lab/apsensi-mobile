package com.threelab.apsensi

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.io.ByteArrayOutputStream

class EditProfilActivity: AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profil)
        supportActionBar?.hide()

        val simpanPerubahan_button: Button = findViewById(R.id.simpanPerubahan_button)
        simpanPerubahan_button.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id) {
                R.id.simpanPerubahan_button -> {
                    // Ambil gambar dari ImageView
                    val imageView: ImageView = findViewById(R.id.framefoto)
                    val bitmap = (imageView.drawable as BitmapDrawable).bitmap

                    // Panggil fungsi uploadImage
                    uploadImage(bitmap)

                    // Pindah ke aktivitas ProfilActivity
                    val pindahIntent = Intent(this, ProfilActivity::class.java)
                    startActivity(pindahIntent)
                }
            }
        }
    }
    fun selectImage(view: View) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
            Companion.PICK_IMAGE_REQUEST
        )
    }

    // Handle hasil pemilihan gambar
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Companion.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val uri = data.data
            val imageView: ImageView = findViewById(R.id.framefoto)
            imageView.setImageURI(uri)
        }
    }
    private fun uploadImage(bitmap: Bitmap) {
        val url = "https://apsensi.irsyadulibad.my.id/api"

        // Mengubah gambar menjadi byte array
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()

        // Menggunakan Base64 untuk mengkodekan byte array gambar menjadi string
        val encodedImage = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT)

        // Menggunakan Volley untuk mengirim data gambar ke server
        val request = object : StringRequest(
            Request.Method.POST,
            url,
            Response.Listener { response ->
                // Handle response dari server
                Log.d("UPLOAD_RESPONSE", response)
            },
            Response.ErrorListener { error ->
                // Handle error yang terjadi saat upload
                Log.e("UPLOAD_ERROR", "Error uploading image: $error")
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                // Menambahkan parameter yang dibutuhkan oleh server (sesuaikan dengan kebutuhan)
                val params = HashMap<String, String>()
                params["image"] = encodedImage // Ganti "image" dengan nama parameter yang digunakan oleh server
                return params
            }
        }

        // Menambahkan request ke antrian Volley
        Volley.newRequestQueue(this).add(request)
    }

    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }


}
