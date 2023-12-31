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
import com.threelab.apsensi.Helper.FileHelper
import com.threelab.apsensi.Helper.FileUploader
import com.threelab.apsensi.Helper.PreferencesHelper
import org.json.JSONObject
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
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var pdfViewer: PDFView
    private lateinit var sendButton: Button
    private lateinit var sharedPref: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_izin)
        supportActionBar?.hide()

        val arrayAdapter = ArrayAdapter(this@IzinActivity, android.R.layout.simple_spinner_dropdown_item, items)
        excuseDate = Calendar.getInstance()

        calendarButton = findViewById(R.id.izinCalendarBtn)
        calendarText = findViewById(R.id.izinCalendarText)
        izinDescription = findViewById(R.id.izinDeskripsi)
        izinDay = findViewById(R.id.izinDay)
        izinAttachment = findViewById(R.id.izinButtonAttachment)
        izinAttachmentContainer = findViewById(R.id.izinButtonContainer)
        izinSpinner = findViewById(R.id.izinSpinner)
        loadingDialog = LoadingDialog(this)
        pdfViewer = findViewById(R.id.izinPdfViewer)
        sendButton = findViewById(R.id.izinSend)
        sharedPref = PreferencesHelper(this)

        izinAttachment.setOnClickListener { pdfPickIntent() }
        sendButton.setOnClickListener { validateInput() }

        calendarText.text = getSelectedDate()

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

    private fun getSelectedDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(excuseDate?.time)
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
        loadingDialog.showLoading()

        val endpoint = Constant.API_ENDPOINT + "/attendances/excuse"
        val authToken = sharedPref.getString(Constant.PREF_TOKEN) ?: ""

        val params = hashMapOf<String, String>(
            "day" to izinDay.text.toString(),
            "type" to excuseType.toString(),
            "description" to izinDescription.text.toString(),
        )

        val file: ByteArray = pdfUri?.let { FileHelper().pdfToByteArray(this, it) }!!

        FileUploader(this).uploadFileWithText(
            endpoint,
            authToken,
            file,
            "file",
            "izin.pdf",
            "application/pdf",
            params,
            { response ->
                val intent = Intent(this, ExcuseSuccessActivity::class.java)

                loadingDialog.hideLoading()
                startActivity(intent)
            },
            { error ->
                val response = JSONObject(String(error.networkResponse?.data ?: ByteArray(0)))
                val message = response.getJSONObject("meta").getString("message")

                loadingDialog.hideLoading()
                showToast(message)
            }
        )
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