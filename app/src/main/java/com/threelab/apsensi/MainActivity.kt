package com.threelab.apsensi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.threelab.apsensi.Helper.Constant
import com.threelab.apsensi.Helper.PreferencesHelper
import com.threelab.apsensi.data.Employee
import com.threelab.apsensi.data.SessionData
import org.json.JSONObject


class  MainActivity : AppCompatActivity() {

    private lateinit var sharedpref: PreferencesHelper
    private lateinit var requestQueue: RequestQueue
    private lateinit var errorMessageTextView : TextView
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        
        sharedpref = PreferencesHelper(this@MainActivity)
        requestQueue = Volley.newRequestQueue(this@MainActivity)
        loadingDialog = LoadingDialog(this@MainActivity)

        val username: EditText = findViewById(R.id.input_username);
        val password: EditText = findViewById(R.id.input_password);
        val loginBtn: Button = findViewById(R.id.login_button);
        val forgotpassBtn: Button = findViewById(R.id.btnlupapass);
        errorMessageTextView = findViewById(R.id.error_message)

        loginBtn.setOnClickListener { view ->
            val usernameInput = username.text.toString()
            val passwordInput = password.text.toString()

            when {
                usernameInput.isEmpty() && passwordInput.isEmpty() -> {
                    // Case 1: Username dan password tidak diisi
                    errorMessageTextView.text = "Harap isi semua kolom terlebih dahulu"
                }
                usernameInput.isEmpty() -> {
                    // Case 2: Username tidak diisi
                    errorMessageTextView.text = "Username harus diisi!"
                }
                passwordInput.isEmpty() -> {
                    // Case 3: Password tidak diisi
                    errorMessageTextView.text = "Password harus diisi terlebih dahulu"
                }

                else -> {
                    // Case 4: Validasi berhasil, lakukan login
                    errorMessageTextView.text = ""
                    login(usernameInput, passwordInput)
                }
            }
        }

        forgotpassBtn.setOnClickListener {
            val intent = Intent(this, ForgotActivity::class.java)
            startActivity(intent)
        }
    }
    private fun validateInput(username: String, password: String): Boolean {
        return username.isNotEmpty()
    }

    override fun onStart() {
        super.onStart()

        sharedpref = PreferencesHelper(this@MainActivity)
        requestQueue = Volley.newRequestQueue(this@MainActivity)

        if (sharedpref.getString(Constant.PREF_TOKEN)?.isNotEmpty() == true) {
            getUser()
        }
    }

    fun getUser(): Boolean {
        val loginUrl = Constant.API_ENDPOINT + "/user"
        loadingDialog.showLoading()

        // Membuat HashMap untuk menyimpan header
        val headers = HashMap<String, String>()
        headers["Authorization"] = sharedpref.getString(Constant.PREF_TOKEN).toString() // Ganti dengan token akses yang sesuai

        // Contoh menggunakan JsonObjectRequest (mungkin Anda perlu menyesuaikan dengan kebutuhan)
        val request = object : JsonObjectRequest(Request.Method.GET, loginUrl, null,
            { response ->
                val employeeJson = response.getJSONObject("data").getJSONObject("user");

                SessionData.saveEmployee(employeeJson.toString())
                loadingDialog.hideLoading()

                startActivity(Intent(this@MainActivity, BerandaActivity::class.java))
                finish()
            },
            { error ->
                sharedpref.delete(Constant.PREF_TOKEN)
                loadingDialog.hideLoading()
            }) {

            override fun getHeaders(): MutableMap<String, String> {
                return headers
            }
        }

        // Tambahkan permintaan ke antrian
        requestQueue.add(request)

        // Kembalikan nilai sesuai kebutuhan
        // Misalnya, Anda dapat mengembalikan true jika permintaan berhasil dijalankan, dan false sebaliknya.
        return true
    }




    private fun login(username: String, password: String) {
        loadingDialog.showLoading()
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

                getUser();
            },
            { error ->
                val response = JSONObject(String(error.networkResponse?.data ?: ByteArray(0)))
                val errorMessage = response.getJSONObject("meta").getString("message")

                loadingDialog.hideLoading()

                when {
                    errorMessage.contains("username") && errorMessage.contains("password") -> {
                        // Case 1: Username dan password salah
                        errorMessageTextView.text = "Silahkan cek kembali username dan password anda"
                    }
                    errorMessage.contains("password") -> {
                        // Case 2: Password salah
                        errorMessageTextView.text = "Password yang anda masukkan salah"
                    }
                    errorMessage.contains("username") -> {
                        // Case 3: Username salah
                        errorMessageTextView.text = "Username yang anda masukkan salah"
                    }
                    else -> {
                        // Case default: Tampilkan pesan kesalahan umum
                        errorMessageTextView.text = "Akun anda belum terdaftar"
                    }
                }

                Log.e("LoginError", error.toString())
            });

        requestQueue.add(loginRequest)

    }
}
