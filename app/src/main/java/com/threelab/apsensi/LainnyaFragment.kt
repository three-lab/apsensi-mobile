package com.threelab.apsensi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LainnyaFragment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_lainnya)
        supportActionBar?.hide()
    }
}