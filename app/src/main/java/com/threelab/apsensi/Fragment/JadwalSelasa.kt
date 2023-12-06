package com.threelab.apsensi.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.threelab.apsensi.R
import com.threelab.apsensi.data.SessionManager

class JadwalSelasa : Fragment() {

    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_jadwal, container, false)

        // Ganti dengan kode hari yang sesuai di aplikasi
        val dayOfWeek = 2

        // SessionSchedule 2
        val classroomSelasa_schudule1: TextView = rootView.findViewById(R.id.classroomSelasa_schudule1)
        val subjectSelasa_schedule1: TextView = rootView.findViewById(R.id.subjectSelasa_schedule1)
        val startSelasa_schedule1: TextView = rootView.findViewById(R.id.startSelasa_schedule1)
        val endSelasa_schedule1: TextView = rootView.findViewById(R.id.startSelasa_schedule1)

        // Ganti SessionManager.getInstance(context) sesuai dengan implementasi Anda
        val sessionManager = SessionManager.getInstance(requireContext())

        // Ganti getScheduleByDay sesuai dengan implementasi sesungguhnya
//        val scheduleForMonday = sessionManager.getScheduleByDay(dayOfWeek)
//
//        if (scheduleForMonday != null) {
//            classroomSelasa_schudule1.text = scheduleForMonday.classroom
//            subjectSelasa_schedule1.text = scheduleForMonday.subject
//            startSelasa_schedule1.text = scheduleForMonday.start
//            endSelasa_schedule1.text = scheduleForMonday.end
//        } else {
//            val message = "Tidak Ada Jadwal untuk Hari Selasa"
//
//            classroomSelasa_schudule1.text = message
//        }

        return rootView
    }
}


