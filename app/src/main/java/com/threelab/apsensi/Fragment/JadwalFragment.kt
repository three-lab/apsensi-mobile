package com.threelab.apsensi.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.threelab.apsensi.R
import com.threelab.apsensi.data.Schedule
import com.threelab.apsensi.data.SessionManager

class JadwalFragment : Fragment() {

    private lateinit var applicationContext: Context
    private lateinit var rootView: View

    private var scheduleForMonday: List<Schedule>? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        applicationContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_jadwal , container , false)


        // Ganti 1 dengan kode hari Senin yang sesuai di aplikasi Anda
        val dayOfWeek = 1

        // SessionSchedule 1
        val daySenin: TextView = rootView.findViewById(R.id.daySenin)
        val classroomSenin_schudule1: TextView = rootView.findViewById(R.id.classroomSenin_schudule1)
        val subjectSenin_schedule1: TextView = rootView.findViewById(R.id.subjectSenin_schedule1)
        val startSenin_schedule1: TextView = rootView.findViewById(R.id.startSenin_schedule1)
        val endSenin_schedule1: TextView = rootView.findViewById(R.id.startSenin_schedule1)
        val icon1: TextView = rootView.findViewById(R.id.icon1)
        val icon2: TextView = rootView.findViewById(R.id.icon2)
        val icon3: TextView = rootView.findViewById(R.id.icon3)

        // Ganti SessionManager.getInstance(context) sesuai dengan implementasi Anda
        val sessionManager = SessionManager.getInstance(applicationContext!!)

        // Ganti getScheduleByDay sesuai dengan implementasi sesungguhnya
        sessionManager.getScheduleByDay(dayOfWeek, this)

        if (scheduleForMonday != null) {
        } else {
            val message = "Tidak Ada Jadwal untuk Hari Senin"
            classroomSenin_schudule1.text = message

            daySenin.visibility = View.GONE
            subjectSenin_schedule1.visibility = View.GONE
            startSenin_schedule1.visibility = View.GONE
            endSenin_schedule1.visibility = View.GONE
            icon1.visibility = View.GONE
            icon2.visibility = View.GONE
            icon3.visibility = View.GONE
        }


        val selasaCard: CardView = rootView.findViewById(R.id.selasaCard)
        selasaCard.setOnClickListener {
            // Tangani peristiwa klik CardView
            val intent = Intent(activity, JadwalSelasa::class.java)
            startActivity(intent)
        }

        val rabuCard: CardView = rootView.findViewById(R.id.rabuCard)
        rabuCard.setOnClickListener {
            // Tangani peristiwa klik CardView
            val intent = Intent(activity, JadwalRabu::class.java)
            startActivity(intent)
        }

        val kamisCard: CardView = rootView.findViewById(R.id.kamisCard)
        kamisCard.setOnClickListener {
            // Tangani peristiwa klik CardView
            val intent = Intent(activity, JadwalKamis::class.java)
            startActivity(intent)
        }

        val jumatCard: CardView = rootView.findViewById(R.id.jumatCard)
        jumatCard.setOnClickListener {
            // Tangani peristiwa klik CardView
            val intent = Intent(activity, JadwalJumat::class.java)
            startActivity(intent)
        }

        return rootView

    }
    fun onSuccess(data: List<Schedule>) {
        // Tangani respons sukses di sini
        // Perbarui UI dengan data jadwal
        scheduleForMonday = data

        // Gunakan data sesuai kebutuhan Anda, contoh:
        val daySenin: TextView = rootView.findViewById(R.id.daySenin)
        val classroomSenin_schudule1: TextView =
            rootView.findViewById(R.id.classroomSenin_schudule1)
        val subjectSenin_schedule1: TextView = rootView.findViewById(R.id.subjectSenin_schedule1)
        val startSenin_schedule1: TextView = rootView.findViewById(R.id.startSenin_schedule1)
        val endSenin_schedule1: TextView = rootView.findViewById(R.id.startSenin_schedule1)

        if (scheduleForMonday != null && scheduleForMonday!!.isNotEmpty()) {
            val schedule = scheduleForMonday!![0]
            daySenin.text = schedule.day
            classroomSenin_schudule1.text = schedule.classroom
            subjectSenin_schedule1.text = schedule.subject
            startSenin_schedule1.text = schedule.start
            endSenin_schedule1.text = schedule.end
        } else {
            val message = "Tidak Ada Jadwal untuk Hari Senin"
            classroomSenin_schudule1.text = message
            // Sisanya dari kode Anda
        }


        fun onError(errorMessage: String) {
            // Tangani respons error di sini
            // Tampilkan pesan kesalahan atau ambil tindakan yang sesuai
            // ...
        }
    }
}




