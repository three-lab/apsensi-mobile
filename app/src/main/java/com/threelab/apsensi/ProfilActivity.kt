package com.threelab.apsensi

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.threelab.apsensi.data.SessionData

class ProfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)
        supportActionBar?.hide()

        val textnama: TextView = findViewById(R.id.textnama)
        val textemail: TextView = findViewById(R.id.textemail)
        val nama_text: TextView = findViewById(R.id.nama_text)
        val textNIK: TextView = findViewById(R.id.textNIK)
        val namaPanggil_text: TextView = findViewById(R.id.namaPanggil_text)
        val tempatlahir_text: TextView = findViewById(R.id.tempatlahir_text)
        val tanggal_text: TextView = findViewById(R.id.tanggal_text)
        val alamat_text: TextView = findViewById(R.id.alamat_text)


        textnama.text = SessionData.getEmployee()?.fullname
        textemail.text = SessionData.getEmployee()?.email
        nama_text.text = SessionData.getEmployee()?.fullname
        textNIK.text = SessionData.getEmployee()?.nik
        namaPanggil_text.text = SessionData.getEmployee()?.username
        tempatlahir_text.text = SessionData.getEmployee()?.birthplace
        tanggal_text.text = SessionData.getEmployee()?.birthdate
        alamat_text.text = SessionData.getEmployee()?.address


    }
}

