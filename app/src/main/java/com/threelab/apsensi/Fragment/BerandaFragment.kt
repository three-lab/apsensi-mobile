package com.threelab.apsensi.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.threelab.apsensi.CalendarActivity
import com.threelab.apsensi.FaqActivty
import com.threelab.apsensi.Helper.Constant
import com.threelab.apsensi.IzinActivity
import com.threelab.apsensi.LaporanActivity
import com.threelab.apsensi.R

class BerandaFragment : Fragment() {

    lateinit var requestQueue: RequestQueue

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_beranda, container, false)

        requestQueue = Volley.newRequestQueue(requireContext())

        getDataLogin()

        // Declare CardView instances
        val kalenderCard: CardView = view.findViewById(R.id.kalenderCard)
        val laporanCard: CardView = view.findViewById(R.id.laporanCard)
        val bantuanCard: CardView = view.findViewById(R.id.bantuanCard)
        val izinCard: CardView = view.findViewById(R.id.izinCard)

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
        return view
    }
    private fun getDataLogin() {
        val loginUrl = Constant.API_ENDPOINT + "/user"
        val request = JsonObjectRequest(
            Request.Method.GET, loginUrl, null,
            { response ->
                // Tangani respons sukses di sini
                val username = response.getString("username")
                val email = response.getString("email")

                // Contoh menampilkan data ke log
                Log.d("UserData", "Username: $username, Email: $email")
            },
            { error ->
                // Tangani kesalahan di sini
                if (error.networkResponse?.statusCode == 401) {
                    // Token kedaluwarsa atau tidak valid
                    // Lakukan tindakan yang sesuai, seperti meminta pengguna untuk login kembali
                    Toast.makeText(requireContext(), "Token expired, please login again", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Token expired, please login again", Toast.LENGTH_SHORT).show()
                }
            })

        // Tambahkan permintaan ke antrian
        requestQueue.add(request)
    }
}
