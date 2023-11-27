package com.threelab.apsensi

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class IzinActivity : AppCompatActivity() {
    private val items = arrayOf("Sakit", "Dinas Luar Kota", "Rapat Dinas", "Lainnya (Tulis di deskripsi)")

    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var adapterItem: ArrayAdapter<String>
    private lateinit var ambilTanggal: EditText
    private lateinit var pilihTanggal: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_izin)
        supportActionBar?.hide()

        autoCompleteTextView = findViewById(R.id.auto_complete_txt)
        adapterItem = ArrayAdapter(this, R.layout.list_item_jenisizin, items)

        autoCompleteTextView.setAdapter(adapterItem)

        ambilTanggal = findViewById(R.id.ambilTanggal)
        pilihTanggal = findViewById(R.id.pilihTanggal)

        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            // Handler untuk item yang dipilih
            val selectedItem = parent.getItemAtPosition(position).toString()
            // Lakukan sesuatu dengan item yang dipilih

            // Tampilkan pesan Toast untuk item yang dipilih
            Toast.makeText(this@IzinActivity, "Item: $selectedItem", Toast.LENGTH_SHORT).show()
        }

        pilihTanggal.setOnClickListener(View.OnClickListener {
            // Lakukan sesuatu ketika tombol diklik
            val calendar = Calendar.getInstance()
            val tahun = calendar.get(Calendar.YEAR)
            val bulan = calendar.get(Calendar.MONTH)
            val tanggal = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    // Tangani pemilihan tanggal disini
                    calendar.set(year, month, dayOfMonth)

                    val selectedDate = getSelectedDate(calendar)
                    ambilTanggal.setText(selectedDate)
                },
                tahun,
                bulan,
                tanggal
            )

            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()
        })
    }

    private fun getSelectedDate(calendar: Calendar): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}