package com.threelab.apsensi

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class IzinActivity : AppCompatActivity() {
    private val items = arrayOf("Sakit", "Dinas Luar Kota", "Rapat Dinas", "Lainnya (Tulis di deskripsi)")

    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var adapterItem: ArrayAdapter<String>
    private lateinit var ambilTanggal: EditText
    private lateinit var pilihTanggal: ImageButton
    private lateinit var buttonChooseImage: Button

    private val PICK_FILE_REQUEST = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_izin)
        supportActionBar?.hide()

        autoCompleteTextView = findViewById(R.id.auto_complete_txt)
        adapterItem = ArrayAdapter(this, R.layout.list_item_jenisizin, items)

        autoCompleteTextView.setAdapter(adapterItem)

        ambilTanggal = findViewById(R.id.ambilTanggal)
        pilihTanggal = findViewById(R.id.pilihTanggal)

        buttonChooseImage = findViewById(R.id.buttonChooseImage)

        // Pengaturan listener untuk tombol pemilihan gambar
        buttonChooseImage.setOnClickListener {
            // Intent untuk memilih gambar atau file PDF dari penyimpanan
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "application/pdf"))
            startActivityForResult(intent, PICK_FILE_REQUEST)
        }

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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Mendapatkan URI file yang dipilih
            val fileUri: Uri = data.data!!

            // Cek tipe MIME untuk menentukan apakah itu gambar atau file PDF
            val mimeType = contentResolver.getType(fileUri)
            if (mimeType?.startsWith("image/") == true) {
                // Itu adalah gambar
                val imageView: ImageView = findViewById(R.id.pdfIconImageView)
                imageView.setImageURI(fileUri)
                kirimDataKeAPI()
            } else if (mimeType?.equals("application/pdf") == true) {

                val pdfIconImageView: ImageView = findViewById(R.id.pdfIconImageView)

                // Simpan URI file PDF untuk penggunaan nanti
                val pdfFileUri: Uri = data.data!!
                // Lakukan sesuatu dengan URI file PDF, seperti menyimpannya atau memprosesnya nanti
                showToast("File PDF Terpilih: $pdfFileUri")
            } else {
                // Tipe file tidak didukung
                showToast("Tipe file tidak didukung")
            }
        }
    }
    private fun kirimDataKeAPI() {
        val jenisIzin = autoCompleteTextView.text.toString()
        val deskripsiIzin = findViewById<EditText>(R.id.deskripsiIzin).text.toString()
        val tanggal = ambilTanggal.text.toString()

        // Ganti URL_API dengan URL sebenarnya dari API Anda
        val url = "https://apsensi.my.id/api/attendance/excuse"

        val jsonObject = JSONObject()
        jsonObject.put("jenis_izin", jenisIzin)
        jsonObject.put("deskripsi", deskripsiIzin)
        jsonObject.put("tanggal", tanggal)

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            Response.Listener { response ->
                // Handle respons sukses
                showToast("Data berhasil dikirim")
            },
            Response.ErrorListener { error ->
                // Handle respons gagal
                showToast("Gagal mengirim data: ${error.message}")
            })

        // Tambahkan request ke antrian Volley
        Volley.newRequestQueue(this).add(request)
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}