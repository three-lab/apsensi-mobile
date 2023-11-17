package com.threelab.apsensi

import android.os.Bundle
import android.widget.CalendarView
import android.widget.CalendarView.OnDateChangeListener
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

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

        backberanda.setOnClickListener{
            if (supportFragmentManager.backStackEntryCount> 0){
                supportFragmentManager.popBackStack()
            } else {
                onBackPressed()
            }
        }

        calendarView.setOnDateChangeListener(OnDateChangeListener{view, year, month, dayOfMonth ->
            val Date = (dayOfMonth.toString() + "-" + (month + 1) + "-" + year)
        })
    }
}
