package com.threelab.apsensi

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnRenderListener
import com.threelab.apsensi.Helper.Constant
import com.threelab.apsensi.Helper.FileUploader
import com.threelab.apsensi.Helper.PreferencesHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class IzinActivity : AppCompatActivity() {
    private val items = arrayOf("Sakit", "Dinas Luar Kota", "Rapat Dinas", "Lainnya")
    private var excuseDate: Calendar? = null
    private var excuseType: String? = null
    private var pdfUri: Uri? = null

    private lateinit var calendarButton: ImageButton
    private lateinit var calendarText: TextView
    private lateinit var izinDescription: EditText
    private lateinit var izinDay: EditText
    private lateinit var izinAttachment: ImageButton
    private lateinit var izinAttachmentContainer: RelativeLayout
    private lateinit var izinSpinner: Spinner
    private lateinit var pdfViewer: PDFView
    private lateinit var sendButton: Button
    private lateinit var sharedPref: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_izin)
        supportActionBar?.hide()

        val arrayAdapter = ArrayAdapter(this@IzinActivity, android.R.layout.simple_spinner_dropdown_item, items)

        calendarButton = findViewById(R.id.izinCalendarBtn)
        calendarText = findViewById(R.id.izinCalendarText)
        izinDescription = findViewById(R.id.izinDeskripsi)
        izinDay = findViewById(R.id.izinDay)
        izinAttachment = findViewById(R.id.izinButtonAttachment)
        izinAttachmentContainer = findViewById(R.id.izinButtonContainer)
        izinSpinner = findViewById(R.id.izinSpinner)
        pdfViewer = findViewById(R.id.izinPdfViewer)
        sendButton = findViewById(R.id.izinSend)
        sharedPref = PreferencesHelper(this)

        izinAttachment.setOnClickListener { pdfPickIntent() }
        calendarButton.setOnClickListener { showDatePicker() }
        calendarText.setOnClickListener { showDatePicker() }
        sendButton.setOnClickListener { validateInput() }

        izinSpinner.adapter = arrayAdapter
        izinSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                excuseType = items[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, monthOfYear)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                excuseDate = selectedDate
                calendarText.text = getSelectedDate(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun getSelectedDate(calendar: Calendar): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun pdfPickIntent() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT

        pdfActivityResultLauncher.launch(intent)
    }

    private fun sendExcuse() {
        val endpoint = Constant.API_ENDPOINT + "/attendances/excuse"
        val authToken = sharedPref.getString(Constant.PREF_TOKEN)

        val params = hashMapOf<String, String>(
            "day" to izinDay.text.toString(),
            "type" to excuseType.toString(),
            "description" to izinDescription.text.toString(),
        )

//        FileUploader(this).uploadFileWithText(
//            endpoint,
//            authToken,
//
//        )
    }

    private fun validateInput() {
        when {
            excuseType == null -> {
                showToast("Tipe izin harus diisi")
            }

            izinDescription.text.toString().isEmpty() -> {
                showToast("Deskripsi izin harus diisi")
            }

            excuseDate == null -> {
                showToast("Tanggal harus diisi")
            }

            izinDay.text.toString().isEmpty() -> {
                showToast("Lama hari izin harus diisi")
            }

            pdfUri == null -> {
                showToast("Surat izin harus diisi")
            }

            else -> { sendExcuse() }
        }
    }

    val pdfActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if(result.resultCode == RESULT_OK) {
                pdfUri = result.data!!.data

                izinAttachmentContainer.alpha = 0.5f
                pdfViewer.fromUri(pdfUri)
                    .onRender(object : OnRenderListener {
                        override fun onInitiallyRendered(pages: Int, pageWidth: Float, pageHeight: Float) {
                            pdfViewer.fitToWidth()
                        }
                    })
                    .load()
            }
        }
    )
}