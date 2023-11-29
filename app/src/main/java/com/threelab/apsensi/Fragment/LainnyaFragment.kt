package com.threelab.apsensi.Fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.threelab.apsensi.Helper.Constant
import com.threelab.apsensi.Helper.PreferencesHelper
import com.threelab.apsensi.MainActivity
import com.threelab.apsensi.ProfilActivity
import com.threelab.apsensi.R
import com.threelab.apsensi.data.SessionData

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
        val namaAkun: TextView = root.findViewById(R.id.namaakun)
        val email_pengguna: TextView = root.findViewById(R.id.email_pengguna)
        val fotoProfil: ImageView = root.findViewById(R.id.fotoProfil)
        val toprofil: ImageButton = root.findViewById(R.id.toprofil)

        namaAkun.text = SessionData.getEmployee()?.fullname
        email_pengguna.text = SessionData.getEmployee()?.email

        toprofil.setOnClickListener{
            val intent = Intent(activity, ProfilActivity::class.java)
            startActivity(intent)
        }

        // Ambil objek Employee dari SessionData
        val employee = SessionData.getEmployee()

        if (employee != null) {
            val photos = employee.photos
            if (photos != null) {
                val frontPhoto = photos.front
                val leftPhoto = photos.left
                val rightPhoto = photos.right
            }
        }


                btnLogout.setOnClickListener {
            clearToken()

            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)

            activity?.finish()
        }
        return root
    }

    private fun clearToken(){
        // Dapatkan instance SharedPreferences
        sharedPref.delete(Constant.PREF_TOKEN)
    }
}