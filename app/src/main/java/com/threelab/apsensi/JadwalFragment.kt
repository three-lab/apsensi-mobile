package com.threelab.apsensi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class JadwalFragment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_jadwal)
        supportActionBar?.hide()
    }
}