package com.threelab.apsensi


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class OtpActivity : AppCompatActivity() {

    private lateinit var editTextOtp: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_otppass)

        editTextOtp = findViewById(R.id.edit_otp)
        val btnVerifyOtp: Button = findViewById(R.id.btn_lanjut)
        val backlogin: TextView = findViewById(R.id.backlogin)

        backlogin.setOnClickListener {
            val intent = Intent(this , MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Generate random 6-digit OTP
        val generatedOtp = generateOtp()

        // Simpan OTP ke dalam shared preferences atau sesuai kebutuhan
        saveOtpToSharedPreferences(generatedOtp)

        // Tampilkan OTP pada EditText atau TextView
        editTextOtp.setText(generatedOtp)

        btnVerifyOtp.setOnClickListener {
            val userEnteredOtp = editTextOtp.text.toString()
            val generatedOtp =
                getStoredOtpFromSharedPreferences() // Gantilah dengan metode yang benar untuk mendapatkan OTP yang disimpan

            if (userEnteredOtp == generatedOtp) {
                showToast("OTP Verified Successfully")

                // OTP benar, arahkan ke halaman DialogNewPassActivity
//                val intent = Intent(this ,newpass::class.java)
//                startActivity(intent)
//                finish()
            } else {
                showToast("Incorrect OTP. Please try again.")
            }
        }
    }

    private fun generateOtp(): String {
        // Generate random 6-digit OTP
        return (100000..999999).random().toString()
    }

    private fun saveOtpToSharedPreferences(otp: String) {
        // Simpan OTP ke dalam shared preferences atau sesuai kebutuhan
        // Implementasi disesuaikan dengan kebutuhan aplikasi Anda
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun getStoredOtpFromSharedPreferences(): String {
        // Implementasi disesuaikan dengan kebutuhan aplikasi Anda
        // Mengambil OTP yang telah disimpan dari SharedPreferences
        return "123456" // Gantilah dengan metode yang benar untuk mendapatkan OTP yang disimpan
    }
}