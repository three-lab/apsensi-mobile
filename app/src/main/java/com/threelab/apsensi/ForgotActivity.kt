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
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.threelab.apsensi.Helper.Constant
import com.threelab.apsensi.Helper.PreferencesHelper
import org.json.JSONObject
import kotlin.random.Random

class ForgotActivity : AppCompatActivity() {

    private lateinit var sharedpref: PreferencesHelper
    private lateinit var requestQueue: RequestQueue
    private var sentEmail: String? = null
    private var code: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)
        supportActionBar?.hide()

        val btnLanjut: Button = findViewById(R.id.btn_lanjut)
        val username: EditText = findViewById(R.id.edit_username)
        val backlogin: Button = findViewById(R.id.backlogin)

        btnLanjut.setOnClickListener { view ->
            forgotPass(username.text.toString())
            val random = Random
            code = random.nextInt(8999)+1000
            forgotPass(username.text.toString())

            val dataRequest = JSONObject()
            dataRequest.put("username", username.text.toString());

            val requestQueue = Volley.newRequestQueue(applicationContext)

            val stringRequest = StringRequest(
                Request.Method.POST,
                "https://apsensi.my.id/api/forgot-pass",
                { response ->
                    Toast.makeText(this@ForgotActivity, ""+response, Toast.LENGTH_SHORT).show()
                },
                { error ->
                    Toast.makeText(applicationContext, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
            requestQueue.add(stringRequest)
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

        if (sharedpref.getString(Constant.PREF_CODE)?.isNotEmpty() == true && sentEmail !=null) {
            if (getOtp(sentEmail.toString())) {
                startActivity(Intent(this, OtpActivity::class.java).apply {
                    putExtra("username", sentEmail)
                })
                finish()
            }
        }
    }

    private fun getOtp(username: String): Boolean {
        val otpUrl = Constant.API_ENDPOINT + "/forgot-pass"

        try {
            val otpData = JSONObject().apply {
                put("username", username)
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

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun forgotPass(username: String) {
        val forgotUrl = Constant.API_ENDPOINT + "/forgot-pass";

        try{
        val dataRequest: JSONObject = JSONObject()
        dataRequest.put("username", username)

        val ForgotPasswordRequest = JsonObjectRequest(
            Request.Method.POST, forgotUrl, dataRequest,
            { response ->
                val meta = response.getJSONObject("meta")
                val data = response.getJSONObject("data")


                this.sentEmail = username  // Simpan email untuk digunakan di OtpActivity

                if (getOtp(username)) {
                    // Langkah 2: Beralih ke OtpActivity
                    startActivity(Intent(this , OtpActivity::class.java))
                    finish()
                }
            },
            { error ->
                // Handle error, if needed
                Toast.makeText(applicationContext, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        // Tambahkan permintaan ke antrian
        requestQueue.add(ForgotPasswordRequest)
    } catch (e: Exception){
        e.printStackTrace()

        }
    }
}