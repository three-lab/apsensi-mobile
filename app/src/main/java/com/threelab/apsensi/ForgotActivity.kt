package com.threelab.apsensi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.threelab.apsensi.Helper.Constant
import com.threelab.apsensi.Helper.PreferencesHelper
import org.json.JSONObject

class ForgotActivity : AppCompatActivity() {

    private lateinit var sharedpref: PreferencesHelper
    private lateinit var requestQueue: RequestQueue
    private var sentEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)
        supportActionBar?.hide()

        val btnLanjut: Button = findViewById(R.id.btn_lanjut)
        val email: EditText = findViewById(R.id.edit_email)
        val backlogin: Button = findViewById(R.id.backlogin)

        btnLanjut.setOnClickListener { view ->
            forgotPass(email.text.toString())
        }

        backlogin.setOnClickListener{
            val intent = Intent(this@ForgotActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        sharedpref = PreferencesHelper(this@ForgotActivity)
        requestQueue = Volley.newRequestQueue(this@ForgotActivity)

        if (sharedpref.getString(Constant.PREF_CODE)?.isNotEmpty() == true) {
            if (getOtp(sentEmail.toString())) {
                startActivity(Intent(this, OtpActivity::class.java).apply {
                    putExtra("email", sentEmail)
                })
                finish()
            }
        }
    }

    private fun getOtp(email: String): Boolean {
        val otpUrl = Constant.API_ENDPOINT + "/forgot-pass"

        val otpData = JSONObject().apply {
            put("email", email)
        }

        val otpRequest = JsonObjectRequest(
            Request.Method.POST, otpUrl, otpData,
            { response ->
                // Handle response jika diperlukan
                Toast.makeText(applicationContext, "OTP Sent Successfully", Toast.LENGTH_SHORT).show()
                // Lakukan navigasi atau langkah selanjutnya setelah OTP dikirim
            },
            { error ->
                // Handle error, if needed
                Toast.makeText(applicationContext, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        // Tambahkan permintaan ke antrian
        requestQueue.add(otpRequest)

        // Kembalikan nilai sesuai kebutuhan
        // Misalnya, Anda dapat mengembalikan true jika permintaan berhasil dijalankan, dan false sebaliknya.
        return true
    }

    private fun forgotPass(email: String) {
        val forgotUrl = Constant.API_ENDPOINT + "/forgotPass";

        val dataRequest: JSONObject = JSONObject();
        dataRequest.put("email", email);

        val ForgotPasswordRequest = JsonObjectRequest(
            Request.Method.POST, forgotUrl, dataRequest,
            { response ->
                val meta = response.getJSONObject("meta")
                val data = response.getJSONObject("data")

                // Dapatkan kode
                val code = meta.getInt("code")

                sharedpref.put(Constant.PREF_CODE, code.toString())
                sentEmail = email  // Simpan email untuk digunakan di OtpActivity

                startActivity(
                    Intent(
                        this,
                        OtpActivity::class.java
                    )
                )
                finish()
            },
            { error ->
                // Handle error, if needed
                Toast.makeText(applicationContext, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        // Tambahkan permintaan ke antrian
        requestQueue.add(ForgotPasswordRequest)
    }
}