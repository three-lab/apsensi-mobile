package com.threelab.apsensi.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.threelab.apsensi.R
import com.threelab.apsensi.ScanActivity2

class AbsenFragment  : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_absen, container, false)

        val cameraScan: Button = view.findViewById(R.id.cameraScan)

        cameraScan.setOnClickListener {
            val intent = Intent(activity, ScanActivity2::class.java)
            startActivity(intent)
        }
        return view
    }
}
