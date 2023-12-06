package com.threelab.apsensi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView

class PresenceFailedActivity: AppCompatActivity() {
    private lateinit var failedAnimation: LottieAnimationView
    private lateinit var messageText: TextView
    private lateinit var backButton: Button

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_failedpresence)
        supportActionBar?.hide()

        failedAnimation = findViewById(R.id.failed_icon)
        messageText = findViewById(R.id.presence_failed_msg)
        backButton = findViewById(R.id.presence_failed_back)

        failedAnimation.scaleX = 3f
        failedAnimation.scaleY = 3f

        backButton.setOnClickListener {
            val intent = Intent(this, BerandaActivity::class.java)

            intent.putExtra("fragmentToLoad", "absenFragment")
            startActivity(intent)
            finish()
        }

        val message = intent.getStringExtra("message")
        if(message != null) messageText.text = "Presensi gagal direkam: " + message
    }
}