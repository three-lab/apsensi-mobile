package com.threelab.apsensi

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class IzinActivity: AppCompatActivity() {
    private val items = arrayOf("Sakit", "Dinas Luar Kota", "Rapat Dinas", "Lainnya (Tulis di deskripsi)")

    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var adapterItem: ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_izin)
        supportActionBar?.hide()

        autoCompleteTextView = findViewById(R.id.auto_complete_txt)
        adapterItem = ArrayAdapter(this, R.layout.list_item_jenisizin, items)

        autoCompleteTextView.setAdapter(adapterItem)

        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            // Handler untuk item yang dipilih
            val selectedItem = parent.getItemAtPosition(position).toString()
            // Lakukan sesuatu dengan item yang dipilih

            // Tampilkan pesan Toast untuk item yang dipilih
            Toast.makeText(this@IzinActivity, "Item: $selectedItem", Toast.LENGTH_SHORT).show()
        }
    }
}