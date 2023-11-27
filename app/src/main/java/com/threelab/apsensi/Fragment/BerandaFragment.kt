package com.threelab.apsensi.Fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.threelab.apsensi.CalendarActivity
import com.threelab.apsensi.FaqActivty
import com.threelab.apsensi.IzinActivity
import com.threelab.apsensi.LaporanActivity
import com.threelab.apsensi.R
import com.threelab.apsensi.data.SessionData
import java.util.Calendar

class BerandaFragment : Fragment() {
    lateinit var waktumasuk: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval: Long = 1000 //update setiap 1 detik

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_beranda, container, false)

        // Declare CardView instances
        val kalenderCard: CardView = view.findViewById(R.id.kalenderCard)
        val laporanCard: CardView = view.findViewById(R.id.laporanCard)
        val bantuanCard: CardView = view.findViewById(R.id.bantuanCard)
        val izinCard: CardView = view.findViewById(R.id.izinCard)
        val namaAkun: TextView = view.findViewById(R.id.namaakun)

        namaAkun.text = SessionData.getEmployee()?.fullname

        // Now you can use kalenderCard and laporanCard as needed

        kalenderCard.setOnClickListener {
            // Your onClick logic here
            // For example, replace the code below with your desired logic

            // Start a new activity (replace with your actual activity class)
            val intent = Intent(activity, CalendarActivity::class.java)
            startActivity(intent)
        }
        laporanCard.setOnClickListener {
            val intent = Intent(activity, LaporanActivity::class.java)
            startActivity(intent)
        }
        bantuanCard.setOnClickListener {
            val intent = Intent(activity, FaqActivty::class.java)
            startActivity(intent)
        }
        izinCard.setOnClickListener {
            val intent = Intent(activity, IzinActivity::class.java)
            startActivity(intent)
        }

        // Inisialisasi TextView
        waktumasuk = view.findViewById(R.id.waktumasuk)

        // Jalankan Runnable untuk memperbarui waktu masuk setiap detik
        handler.postDelayed(runnable, updateInterval)
        return view
    }
    private val runnable = object : Runnable {
        override fun run() {
            // Perbarui TextView dengan waktu masuk saat ini
            updateWaktu()

            // Jalankan kembali Runnable setelah interval tertentu
            handler.postDelayed(this, updateInterval)
        }
    }

    private fun updateWaktu() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        val greeting = when {
            hour >= 5 && hour < 12 -> "Selamat Pagi"
            hour >= 12 && hour < 15 -> "Selamat Siang"
            hour >= 15 && hour < 18 -> "Selamat Sore"
            else -> "Selamat Malam"
        }

        waktumasuk.text = greeting
    }

    override fun onDestroyView() {
        // Hentikan Runnable saat Fragment dihancurkan
        handler.removeCallbacks(runnable)
        super.onDestroyView()
    }
}
