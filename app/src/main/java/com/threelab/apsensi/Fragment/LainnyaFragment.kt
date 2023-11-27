package com.threelab.apsensi.Fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.threelab.apsensi.Helper.Constant
import com.threelab.apsensi.Helper.PreferencesHelper
import com.threelab.apsensi.MainActivity
import com.threelab.apsensi.R
import com.threelab.apsensi.ScanActivity2

class LainnyaFragment : Fragment() {
    private lateinit var sharedPref: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        sharedPref = PreferencesHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_lainnya, container, false)

        val btnLogout: Button = root.findViewById(R.id.logOut)

        btnLogout.setOnClickListener {
            clearToken()

            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)

            activity?.finish()
        }
        return root
    }

    private fun clearToken() {
        // Dapatkan instance SharedPreferences
        sharedPref.delete(Constant.PREF_TOKEN)
    }
}




