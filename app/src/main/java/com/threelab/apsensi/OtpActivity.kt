package com.threelab.apsensi


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.threelab.apsensi.Helper.Constant
import org.json.JSONObject

class OtpActivity : AppCompatActivity() {

    private lateinit var editTextOtp: EditText
    private lateinit var requestQueue: RequestQueue
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_otppass)

        editTextOtp = findViewById(R.id.edit_otp)
        requestQueue = Volley.newRequestQueue(this@OtpActivity)
        loadingDialog = LoadingDialog(this@OtpActivity)

        val btnVerifyOtp: Button = findViewById(R.id.btn_lanjut)
        val backlogin: TextView = findViewById(R.id.backlogin)
        val username = intent?.getStringExtra("username") ?: ""

        backlogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnVerifyOtp.setOnClickListener {
            loadingDialog.showLoading()
            verifyOtp(username, editTextOtp.text.toString())
        }
    }

    private fun verifyOtp(username: String, otp: String) {
        val forgotUrl = Constant.API_ENDPOINT + "/forgot-pass"
        val dataRequest = JSONObject();

        dataRequest.put("username", username)
        dataRequest.put("code", otp)

        val forgotRequest = JsonObjectRequest(Request.Method.POST, forgotUrl, dataRequest,
            { response ->
                Toast.makeText(this, "Kode OTP Berhasil Diverifikasi", Toast.LENGTH_SHORT)
                    .show()
            },
            { error ->

            });

        requestQueue.add(forgotRequest)
    }
}