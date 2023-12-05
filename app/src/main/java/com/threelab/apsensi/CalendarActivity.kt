package com.threelab.apsensi

import android.os.Bundle
import android.widget.CalendarView
import android.widget.CalendarView.OnDateChangeListener
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class CalendarActivity : AppCompatActivity() {
    lateinit var calendarView: CalendarView
    lateinit var textView: TextView
    lateinit var backberanda: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        supportActionBar?.hide()

        calendarView = findViewById(R.id.calendarView)
        backberanda = findViewById(R.id.backberanda)
        textView = findViewById(R.id.libur)

        backberanda.setOnClickListener {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
            } else {
                onBackPressed()
            }
        }

        calendarView.setOnDateChangeListener(OnDateChangeListener { view, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth-${month + 1}-$year"

            // Panggil endpoint API untuk mendapatkan data libur
            getHolidays(selectedDate)
        })
    }

    private fun getHolidays(selectedDate: String) {
        val url = "https://apsensi.my.id/holidays"

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response: JSONArray ->
                // Tangani respons sukses
                handleResponse(response)
            },
            Response.ErrorListener { error ->
                // Tangani kesalahan koneksi atau respons
                handleError(error)
            })

        // Tambahkan request ke antrian
        requestQueue.add(jsonArrayRequest)
    }

    private fun handleResponse(response: JSONArray) {
        val holidayNames = mutableListOf<String>()

        for (i in 0 until response.length()) {
            val holiday: JSONObject = response.getJSONObject(i)
            val holidayName: String = holiday.getString("name")
            holidayNames.add(holidayName)
        }

        textView.text = "Holidays: ${holidayNames.joinToString(", ")}"
    }

    private fun handleError(error: Exception) {
        // Tangani kesalahan koneksi atau respons
        textView.text = "Error: ${error.message}"
    }
}
