package com.threelab.apsensi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class BerandaFragment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment__beranda)
        supportActionBar?.hide()
    }
}