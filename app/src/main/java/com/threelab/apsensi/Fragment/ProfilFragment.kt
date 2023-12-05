package com.threelab.apsensi.data

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.threelab.apsensi.EditProfilActivity
import com.threelab.apsensi.Helper.Constant
import com.threelab.apsensi.Helper.PreferencesHelper
import com.threelab.apsensi.MainActivity
import com.threelab.apsensi.R

class ProfilFragment : Fragment() {
    private lateinit var sharedPref: PreferencesHelper
    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = PreferencesHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_profil, container, false)

        val btnLogout: Button = root.findViewById(R.id.logOut)
        val namaAkun: TextView = root.findViewById(R.id.namaakun)
        val email_pengguna: TextView = root.findViewById(R.id.email_pengguna)
        val fotoProfil: ImageView = root.findViewById(R.id.fotoProfil)
        val toprofil: ImageButton = root.findViewById(R.id.toprofil)


        //inisiasi data profil
        val nama_text: TextView = root.findViewById(R.id.nama_text)
        val namaPanggil_text: TextView = root.findViewById(R.id.namaPanggil_text)
        val textNIK: TextView = root.findViewById(R.id.textNIK)
        val tempatlahir_text: TextView = root.findViewById(R.id.tempatlahir_text)
        val tanggal_text: TextView = root.findViewById(R.id.tanggal_text)
        val alamat_text: TextView = root.findViewById(R.id.alamat_text)


        //ambil data pada sesiondata
        nama_text.text = SessionData.getEmployee()?.fullname
        namaPanggil_text.text = SessionData.getEmployee()?.username
        textNIK.text = SessionData.getEmployee()?.nik
        tempatlahir_text.text = SessionData.getEmployee()?.birthplace
        tanggal_text.text = SessionData.getEmployee()?.birthdate
        alamat_text.text = SessionData.getEmployee()?.address

        toprofil.setOnClickListener{
            val intent = Intent(activity, EditProfilActivity::class.java)
            startActivity(intent)
        }

        // Inisialisasi requestQueue
        requestQueue = Volley.newRequestQueue(requireContext())

        // Ambil objek Employee dari SessionData
        val employee = SessionData.getEmployee()

        if (employee != null) {

            namaAkun.text = employee.fullname
            email_pengguna.text = employee.email

            val photos = employee.photos
            if (photos != null) {
                val frontPhoto = photos.front
                // Panggil metode untuk mengambil gambar
                requestImage(frontPhoto, fotoProfil)
            }
        }

        btnLogout.setOnClickListener{
            clearToken()

            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
        return root
    }

    private fun clearToken(){
        sharedPref.delete(Constant.PREF_TOKEN)
    }

    // Metode untuk mengambil gambar dari URL dan menampilkan di ImageView
    private fun requestImage(imageUrl: String?, imageView: ImageView) {
        if (!imageUrl.isNullOrBlank()) {
            val imageRequest = ImageRequest(
                imageUrl,
                Response.Listener<Bitmap> { response ->
                    // Menangani respons gambar
                    imageView.setImageBitmap(response)
                },
                0,
                0,
                ImageView.ScaleType.CENTER_INSIDE,
                Bitmap.Config.RGB_565,
                Response.ErrorListener { error ->
                    // Menangani kesalahan saat mengambil gambar
                    error.printStackTrace()
                })

            // Menambahkan request ke antrian
            requestQueue.add(imageRequest)
        }
    }
}
