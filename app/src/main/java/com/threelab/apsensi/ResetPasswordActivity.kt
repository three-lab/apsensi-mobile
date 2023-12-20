package com.threelab.apsensi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.threelab.apsensi.Helper.Constant
import org.json.JSONObject

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    private lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        supportActionBar?.hide()

        val EditFirst: EditText = findViewById(R.id.edit_firstPass)
        val EditTwo: EditText = findViewById(R.id.edit_twoPass)
        val btn_reset: Button = findViewById(R.id.btn_resetPass)
        val backReset: Button = findViewById(R.id.backInResetPass)

        requestQueue = Volley.newRequestQueue(this@ResetPasswordActivity)
        loadingDialog = LoadingDialog(this@ResetPasswordActivity)


        backReset.setOnClickListener{
            val intent = Intent(this@ResetPasswordActivity, MainActivity::class.java)
            startActivity(intent)
        }

        btn_reset.setOnClickListener{

            val password = EditFirst.text.toString()
            val confirm = EditTwo.text.toString()
            val data = intent.getStringArrayExtra("data")

            val username = data?.get(0) ?: ""
            val otp = data?.get(1) ?: ""

            if (password.isNotEmpty() && confirm.isNotEmpty()) {
                if (password == confirm) {
                    // Panggil fungsi ResetPassword dengan parameter yang sesuai
                    ResetPassword(username, otp, password, confirm)
                } else {
                    Toast.makeText(this, "Password dan konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Harap isi semua field", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun ResetPassword(username: String, otp: String, password: String, confirm: String){
        loadingDialog.showLoading()

        val forgotUrl = Constant.API_ENDPOINT + "/reset-pass"
        val dataRequest = JSONObject();

        dataRequest.put("username", username)
        dataRequest.put("code", otp)
        dataRequest.put("password", password)
        dataRequest.put("password_confirm", confirm)

        Log.d("Presensi", dataRequest.toString())

        val forgotRequest = JsonObjectRequest(Request.Method.POST, forgotUrl, dataRequest,

            { response ->
                val intent = Intent(this, MainActivity::class.java)
                loadingDialog.hideLoading()

                startActivity(intent)
                finish()
                Toast.makeText(this, "Password berhasil diperbarui", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Toast.makeText(this,"Gagal memproses", Toast.LENGTH_SHORT).show()
                loadingDialog.hideLoading()
            });

        requestQueue.add(forgotRequest)
    }
}