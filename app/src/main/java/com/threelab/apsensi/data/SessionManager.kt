package com.threelab.apsensi.data

import android.content.Context
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.threelab.apsensi.Fragment.JadwalFragment
import org.json.JSONArray

class SessionManager private constructor(private val context: Context) {

    companion object {
        private const val SCHEDULE_API_URL = "https://apsensi.my.id/api/schedules"
        private const val SCHEDULE_KEY = "schedule_key"
        private var instance: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            if (instance == null) {
                instance = SessionManager(context)
            }
            return instance!!
        }
    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun fetchScheduleFromApi() {
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET , SCHEDULE_API_URL , null ,
            { response ->
                val scheduleList = parseResponse(response)
                if (scheduleList.isNotEmpty()) {
                    saveSchedule(scheduleList[0])
                }
            } ,
            { error ->
                Toast.makeText(context , "Error: ${error.message}" , Toast.LENGTH_SHORT).show()
            }
        )

        requestQueue.add(jsonArrayRequest)
    }

    private fun saveSchedule(schedule: Schedule) {
        val gson = Gson()
        val jsonString = gson.toJson(schedule)
        val sharedPreferences = context.getSharedPreferences("MySession" , Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(SCHEDULE_KEY , jsonString).apply()
    }

    private fun parseResponse(response: JSONArray): List<Schedule> {
        val gson = Gson()
        val scheduleList = mutableListOf<Schedule>()

        for (i in 0 until response.length()) {
            val schedule =
                gson.fromJson(response.getJSONObject(i).toString() , Schedule::class.java)
            scheduleList.add(schedule)
        }

        return scheduleList
    }

    fun getScheduleByDay(dayOfWeek: Int , listener: JadwalFragment) {
        val allSchedules = getScheduleList()

        if (allSchedules != null) {
            val scheduleForDay = allSchedules.filter { it.day == dayOfWeek.toString() }
            listener.onSuccess(scheduleForDay)
        } else {
            //listener.onError("Error: Tidak dapat mengambil jadwal dari SharedPreferences")
        }
    }

    fun getAllSchedules(): List<Schedule>? {
        return getScheduleList()
    }

    private fun getScheduleList(): List<Schedule>? {
        val sharedPreferences = context.getSharedPreferences("MySession" , Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString(SCHEDULE_KEY , null)
        return jsonString?.let {
            val gson = Gson()
            val scheduleArray = gson.fromJson(it , Array<Schedule>::class.java)
            scheduleArray.toList()
        }
    }
}

data class Schedule(
    val employee: String,
    val classroom: String,
    val subject: String,
    val day: String,
    val start: String,
    val end: String
)
