package com.threelab.apsensi

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)
        supportActionBar?.hide()

        val btnLanjut: Button = findViewById(R.id.btn_lanjut)
        val username: EditText = findViewById(R.id.input_username)
        val backlogin: Button = findViewById(R.id.backlogin)

        sharedpref = PreferencesHelper(this@ForgotActivity)
        requestQueue = Volley.newRequestQueue(this@ForgotActivity)
        loadingDialog = LoadingDialog(this@ForgotActivity)

        btnLanjut.setOnClickListener { view ->
            loadingDialog.showLoading()
            forgotPassword(username.text.toString())
        }

        backlogin.setOnClickListener{
            val intent = Intent(this@ForgotActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun forgotPassword(username: String) {
        val forgotUrl = Constant.API_ENDPOINT + "/forgot-pass"
        val dataRequest = JSONObject();

        dataRequest.put("username", username);

        val forgotRequest = JsonObjectRequest(Request.Method.POST, forgotUrl, dataRequest,
            { response ->
                val intent = Intent(this, OtpActivity::class.java)

                intent.putExtra("username", username)
                loadingDialog.hideLoading()

                startActivity(intent)
                finish()
            },
            { error ->
                loadingDialog.hideLoading()
                Toast.makeText(this,"Gagal memproses", Toast.LENGTH_SHORT).show()
            });

        requestQueue.add(forgotRequest)
    }
}