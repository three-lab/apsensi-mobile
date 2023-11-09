package com.threelab.apsensi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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

        val username: EditText = findViewById(R.id.input_username);
        val password: EditText = findViewById(R.id.input_password);
        val loginBtn: Button = findViewById(R.id.login_button);

        loginBtn.setOnClickListener { view ->
            login(username.text.toString(), password.text.toString())

            Log.d("token", sharedpref.getString(Constant.PREF_TOKEN).toString())
        }
    }

    override fun onStart() {
        super.onStart()

        sharedpref = PreferencesHelper(this@MainActivity)
        requestQueue = Volley.newRequestQueue(this@MainActivity)

        if (sharedpref.getString(Constant.PREF_TOKEN)?.isNotEmpty() == true) {
            if (getUser()) {
                startActivity(Intent(this, BerandaActivity::class.java))
                finish()
            }
        }
    }

    fun getUser(): Boolean {
        val loginUrl = Constant.API_ENDPOINT + "/user"

        // JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, loginUrl, null,
        //    { response -> {

        //    },

        //      })
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

                startActivity(Intent(this, BerandaActivity::class.java))
                finish()
            },
            { response ->
                val json = JSONObject(String(response.networkResponse.data))
                val meta = json.getJSONObject("meta")

                Log.d("Response", json.toString())
            });

        requestQueue.add(loginRequest)

    }
}
