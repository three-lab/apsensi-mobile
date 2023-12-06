package com.threelab.apsensi

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class EditProfilActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profil)
        supportActionBar?.hide()

        val simpanPerubahan_button: Button = findViewById(R.id.simpanPerubahan_button)
    }
}
