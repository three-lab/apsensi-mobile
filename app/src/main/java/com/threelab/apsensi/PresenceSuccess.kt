package com.threelab.apsensi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView

class PresenceSuccess: AppCompatActivity() {
    private lateinit var successAnimation: LottieAnimationView
    private lateinit var backButton: Button
    private lateinit var messageText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_successpresence)
        supportActionBar?.hide()

        successAnimation = findViewById(R.id.success_icon)
        backButton = findViewById(R.id.presence_success_back)
        messageText = findViewById(R.id.presence_success_msg)

        successAnimation.scaleX = 1.5f
        successAnimation.scaleY = 1.5f

        backButton.setOnClickListener {
            val intent = Intent(this, BerandaActivity::class.java)
            startActivity(intent)
            finish()
        }

        val message = intent.getStringExtra("message")

        if(message != null) messageText.text = message
    }
}