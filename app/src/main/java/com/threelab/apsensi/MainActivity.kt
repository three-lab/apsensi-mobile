package com.threelab.apsensi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val username: EditText = findViewById(R.id.input_username);
        val password: EditText = findViewById(R.id.input_password);
        val loginBtn: Button = findViewById(R.id.login_button);

        loginBtn.setOnClickListener {view ->
            login(username.text.toString(), password.text.toString())
            Log.d("Button", "Button Clicked")
        }
    }

    fun login(username: String, password: String) {
        val loginUrl = "https://apsensi.irsyadulibad.my.id/api/login";
        val requestQueue = Volley.newRequestQueue(this@MainActivity);

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

                Log.d("Response", token)
            },
            { response ->
                val json = JSONObject(String(response.networkResponse.data))
                val meta = json.getJSONObject("meta")

                Log.d("Response", json.toString())
            });

        requestQueue.add(loginRequest)
    }
}