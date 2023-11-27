package com.threelab.apsensi

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.threelab.apsensi.Fragment.AbsenFragment
import com.threelab.apsensi.Fragment.BerandaFragment
import com.threelab.apsensi.Fragment.JadwalFragment
import com.threelab.apsensi.Fragment.LainnyaFragment
import com.threelab.apsensi.data.Employee
import com.threelab.apsensi.data.SessionData

class BerandaActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beranda)
        supportActionBar?.hide()

        bottomNavigationView = findViewById(R.id.bottom_Navigation)

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.beranda -> {
                    replaceFragment(BerandaFragment())
                    true
                }

                R.id.absen -> {
                    replaceFragment(AbsenFragment())
                    true
                }

                R.id.jadwal -> {
                    replaceFragment(JadwalFragment())
                    true
                }

                R.id.lainnya -> {
                    replaceFragment(LainnyaFragment())
                    true
                }
                else -> false
            }
        }
        replaceFragment(BerandaFragment())
    }
    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }
}