package com.threelab.apsensi.Fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.threelab.apsensi.EditProfilActivity
import com.threelab.apsensi.ForgotActivity
import com.threelab.apsensi.Helper.Constant
import com.threelab.apsensi.Helper.PreferencesHelper
import com.threelab.apsensi.MainActivity
import com.threelab.apsensi.R

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
        val gantipassword: Button = root.findViewById(R.id.gantipassword)
        val toprofil: ImageButton = root.findViewById(R.id.toprofil)

        btnLogout.setOnClickListener {
            clearToken()

            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)

            activity?.finish()
        }

        gantipassword.setOnClickListener{
            val intent = Intent(activity, ForgotActivity::class.java)
            startActivity(intent)
        }

        toprofil.setOnClickListener{
            val intent = Intent(activity, EditProfilActivity::class.java)
            startActivity(intent)
        }
        return root
    }

    private fun clearToken(){
        // Dapatkan instance SharedPreferences
        sharedPref.delete(Constant.PREF_TOKEN)
    }
}