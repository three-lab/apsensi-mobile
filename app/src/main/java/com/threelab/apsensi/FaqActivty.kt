package com.threelab.apsensi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog


class FaqActivty: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)
        supportActionBar?.hide()

        val bantuan = arrayOf(
            //1
            "Mengapa sistem tidak dapat mengenali wajah saya?",
            //2
            "Mengapa saya tidak dapat mengakses aplikasi karena masalah koneksi?",
            //3
            " Bagaimana cara mengatasi lupa password?",
            //4
            "Mengapa absensi saya tidak terekam dengan benar?",
            //5
            "Mengapa sistem aplikasi tidak stabil?",
            //6
            "Mengapa absensi saya tidak terekam dengan benar?",
            //7
            "Bagaimana cara mengatasi konflik jadwal absensi?"
        )

        val autoCompleteTextView: AutoCompleteTextView = findViewById(R.id.autocompletetextview)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, bantuan)
        autoCompleteTextView.setAdapter(adapter)

        val helpTextView: TextView = findViewById(R.id.helpTextView)

        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedName = parent.getItemAtPosition(position).toString()
            //tampilkan bantuan terkait dengan pemilihan
           tampilkanBantuanSetelahPemilihan(selectedName, helpTextView )
        }
        val buttonBantuan : Button = findViewById(R.id.buttonBantuan)
        buttonBantuan.setOnClickListener{
            tampilkanDialogBantuan()
        }



    }

    private fun tampilkanBantuanSetelahPemilihan(selectedName: String, helpTextView: TextView) {
        val spasi = " "//2 spasi
        val pesanBantuan = when (selectedName) {
            "Mengapa sistem tidak dapat mengenali wajah saya?" -> "" +
                    "Langkah-langkah penanganan :\n" +
                    "${spasi}${spasi}1. Periksa Pencahayaan.\n" +
                    "-Pastikan pencahayaan disekitar anda mencukupi.\n\n" +
                    "${spasi}${spasi}2. Periksa kondisi wajah.\n" +
                    "-Bersihkan kamera atau sensor wajah untuk memastikan tidak ada hambatan.\n" +
                    "-Pastikan wajah yang didaftarkan sesuai dengan kondisi saat ini\n" +
                    "-Jika ada perubahan yang signifikan, segera hubungi administrator \n\n" +
                    "${spasi}${spasi}3. Bersihkan kamera atau sensor\n" +
                    "-Bersihkan kamera atau sensor wajah dari debu atau kotoran yang mengganggu pengenalan."

            "Mengapa saya tidak dapat mengakses aplikasi karena masalah koneksi?" ->"" +
                    "Langkah-langkah penanganan :\n "+
                    "${spasi}${spasi}1. Periksa koneksi internet Anda dan pastikan stabil.\n" +
                            "${spasi}${spasi}2. Restart perangkat atau router jika diperlukan.\n" +
                    "${spasi}${spasi}3. Pastikan aplikasi memiliki izin akses internet."

            " Bagaimana cara mengatasi lupa password?" -> "" +
                    "Langkah-langkah penanganan :\n"+
                "${spasi}${spasi}1. Gunakan opsi lupa kata sandi.\n" +
                    "-Gunakan opsi lupa kata sandi untuk mengatur ulang password anda. \n\n" +
                    "${spasi}${spasi}2. Periksa email terkait. \n" +
                    "-Pastikan akun anda terverifikasi melalui email. Periksa email untuk petunjuk lebih lanjut. \n\n" +
                    "${spasi}${spasi}3. Hubungi administrator atau dukungan teknis jika perlu bantuan lebih lanjut.\n" +
                    "-Jika masih mengalami kesulitan, hubungi administrator tau dukungan teknis untuk bantuan lebih lanjut."

            "Mengapa absensi saya tidak terekam dengan benar?" -> "" +
                    "Langkah-langkah penanganan : \n"+
                "${spasi}${spasi}1. Pastikan Anda melakukan langkah-langkah absensi dengan benar.\n" +
                    "${spasi}${spasi}2. Periksa apakah ada pembaruan perangkat lunak atau aplikasi yang memperbaiki bug terkait."

            "Bagaimana Cara Mengatasi Konflik Jadwal Absensi?" -> "" +
                    "Langkah-langkah penanganan : \n"+
                "${spasi}${spasi}1. Periksa mekanisme jadwal.\n" +
                        "-Segera hubungi administrator terkait konflik jadwal absensi"

            "Mengapa sistem aplikasi tidak stabil?" ->"" +
                    "Langkah-langkah penanganan : \n" +
                    "${spasi}${spasi}1.Periksa koneksi internet.\n" +
                    "-Pastikan koneksi internet stabil untuk mengindari keterlambatan dalam komunikasi dengan server"

            "Mengapa absensi saya tidak terekam dengan benar?" ->"" +
                    "Langkah-langkah penanganan :\n" +
                    "${spasi}${spasi}1. Pastikan anda melakukan langkah langkah absensi dengan benar. \n" +
                    "${spasi}${spasi}2. Periksa apakah ada pembaharuan perangkat lunak atau aplikasi yang memperbaiki bug terkait."

            "Bagaimana cara mengatasi konflik jadwal absensi?" ->"" +
                    "Langlah-langkah penanganan :\n" +
                    "1. Segera konfirmasikan kepada administrator terkait masalah konflik jadwal absensi. \n"








            // Tambahkan logika bantuan sesuai dengan nama yang dipilih
            else -> "Informasi bantuan untuk $selectedName belum tersedia."
        }
        helpTextView.text = pesanBantuan

    }

    private fun tampilkanDialogBantuan() {
        val pesanBantuan = "Gunakan kotak teks otomatis untuk memilih bantuan."

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Bantuan Penggunaan Aplikasi")
            .setMessage(pesanBantuan)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}




