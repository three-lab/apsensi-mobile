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
import com.android.volley.toolbox.Volley
import com.threelab.apsensi.Helper.Constant
import com.threelab.apsensi.Helper.PreferencesHelper
import org.json.JSONObject


class  MainActivity : AppCompatActivity() {

    private lateinit var sharedpref: PreferencesHelper
    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        
        sharedpref = PreferencesHelper(this@MainActivity)
        requestQueue = Volley.newRequestQueue(this@MainActivity)

        val username: EditText = findViewById(R.id.input_username);
        val password: EditText = findViewById(R.id.input_password);
        val loginBtn: Button = findViewById(R.id.login_button);
        val forgotpassBtn: Button = findViewById(R.id.btnlupapass);

        loginBtn.setOnClickListener { view ->
            login(username.text.toString(), password.text.toString())

            Log.d("token", sharedpref.getString(Constant.PREF_TOKEN).toString())
        }

        forgotpassBtn.setOnClickListener {
            val intent = Intent(this, ForgotActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        sharedpref = PreferencesHelper(this@MainActivity)
        requestQueue = Volley.newRequestQueue(this@MainActivity)

        if (sharedpref.getString(Constant.PREF_TOKEN)?.isNotEmpty() == true) {
            if (getUser()) {
                startActivity(Intent(this, BerandaActivity::class.java))
            }
        }
    }

    fun getUser(): Boolean {
        requestQueue = Volley.newRequestQueue(this@MainActivity)
        val loginUrl = Constant.API_ENDPOINT + "/user"
        // Contoh menggunakan JsonObjectRequest (mungkin Anda perlu menyesuaikan dengan kebutuhan)
        val request = JsonObjectRequest(Request.Method.GET, loginUrl, null,
            { response ->
                Toast.makeText(this@MainActivity.applicationContext, "Login successful", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Log.e("VolleyError", "Error: ${error.networkResponse?.statusCode}, ${String(error.networkResponse?.data ?: ByteArray(0))}")

                if (error.networkResponse?.statusCode == 401) {
                    // Token kedaluwarsa atau tidak valid
                    Toast.makeText(this@MainActivity.applicationContext, "Token kedaluwarsa, silakan login kembali", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity.applicationContext, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        // Tambahkan permintaan ke antrian
        requestQueue.add(request)

        // Kembalikan nilai sesuai kebutuhan
        // Misalnya, Anda dapat mengembalikan true jika permintaan berhasil dijalankan, dan false sebaliknya.
        return true
    }


    private fun login(username: String, password: String) {
        val loginUrl = Constant.API_ENDPOINT + "/login";

        val dataRequest = JSONObject();
        dataRequest.put("username", username);
        dataRequest.put("password", password);

        val loginRequest = JsonObjectRequest(Request.Method.POST, loginUrl, dataRequest,
            { response ->
                val meta = response.getJSONObject("meta")
                val data = response.getJSONObject("data")

                // Dapetin code
                val code = meta.getInt("code")
                // Dapetin token
                val token = data.getString("token")

                sharedpref.put(Constant.PREF_TOKEN, token)

                startActivity(Intent(this@MainActivity, BerandaActivity::class.java))
                finish()
            },
            { error ->
                // Handle error, misalnya:
                Toast.makeText(this@MainActivity, "Login failed: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("LoginError", error.toString())
            });

        requestQueue.add(loginRequest)

    }
}
