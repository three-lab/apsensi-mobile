package com.threelab.apsensi.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.threelab.apsensi.CalendarActivity
import com.threelab.apsensi.R

class BerandaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_beranda, container, false)

        // Declare CardView instances
        val kalenderCard: CardView = view.findViewById(R.id.kalenderCard)
        val laporanCard: CardView = view.findViewById(R.id.laporanCard)

        // Now you can use kalenderCard and laporanCard as needed

        kalenderCard.setOnClickListener {
            // Your onClick logic here
            // For example, replace the code below with your desired logic

            // Start a new activity (replace with your actual activity class)
            val intent = Intent(activity, CalendarActivity::class.java)
            startActivity(intent)
        }
        laporanCard.setOnClickListener {
            val absenFragment = AbsenFragment()

            // Start a fragment transaction
            val transaction = activity?.supportFragmentManager?.beginTransaction()

            // Replace the fragment container with your new fragment (replace R.id.fragment_container with your actual container ID)
            transaction?.replace(R.id.absen, absenFragment)

            // Commit the transaction
            transaction?.commit()
        }
        return view
    }
}
