package com.threelab.apsensi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.threelab.apsensi.R
import com.threelab.apsensi.data.AttendanceItem

class AttendanceAdapter(private val attendanceItems: List<AttendanceItem>) :
    RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_presensi, parent, false)
        return AttendanceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val currentItem = attendanceItems[position]

        // Bind data to the views in item_attendance.xml
        holder.textViewDate.text = "Date: ${currentItem.date}"
    }

    override fun getItemCount() = attendanceItems.size

    class AttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDate: TextView = itemView.findViewById(R.id.tanggalAddAbsen)
    }
}