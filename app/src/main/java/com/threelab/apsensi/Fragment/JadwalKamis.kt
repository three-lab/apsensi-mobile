package com.threelab.apsensi.Fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.threelab.apsensi.R
import com.threelab.apsensi.data.SessionManager

class JadwalKamis : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_jadwalkamis)
        supportActionBar?.hide()

        val sessionManager = SessionManager.getInstance(this)

        val hariKamis: TextView = findViewById(R.id.hariKamis)
        val classroom1: TextView = findViewById(R.id.classroom1)
        val subject1: TextView = findViewById(R.id.subject1)
        val start1: TextView = findViewById(R.id.start1)
        val end1: TextView = findViewById(R.id.end1)

        val dayOfWeek = 4
//        val scheduleForFriday = sessionManager.getScheduleByDay(dayOfWeek)
//
//        if (scheduleForFriday != null) {
//            hariKamis.text = scheduleForFriday.day
//            classroom1.text = scheduleForFriday.classroom
//            subject1.text = scheduleForFriday.subject
//            start1.text = scheduleForFriday.start
//            end1.text = scheduleForFriday.end
//        } else {
//            // Sembunyikan TextView lainnya jika jadwal untuk hari Jumat tidak ditemukan
//            classroom1.visibility = View.GONE
//            subject1.visibility = View.GONE
//            start1.visibility = View.GONE
//            end1.visibility = View.GONE
//
//            // Tampilkan pesan jika jadwal untuk hari Jumat tidak ditemukan
//            val noScheduleMessage = "Mohon maaf, Anda tidak memiliki jadwal ajar untuk hari ini."
//            hariKamis.text = noScheduleMessage
//        }

    }
}