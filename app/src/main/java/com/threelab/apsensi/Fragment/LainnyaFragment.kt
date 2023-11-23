package com.threelab.apsensi.Fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.threelab.apsensi.MainActivity
import com.threelab.apsensi.R
import com.threelab.apsensi.ScanActivity

class LainnyaFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_lainnya, container, false)

        val logOut: Button = view.findViewById(R.id.logOut)

        logOut.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
        return view


    }
}