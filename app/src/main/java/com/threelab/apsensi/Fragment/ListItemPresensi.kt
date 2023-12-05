package com.threelab.apsensi.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.threelab.apsensi.R

class ListItemPresensi : RecyclerView.Adapter<ListItemPresensi.ViewHolder>() {

    val daftarPresensi : MutableList<String> = mutableListOf()
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            // Tentukan komponen UI Anda di sini
        }

        override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_presensi, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // Bind data Anda ke komponen UI
        }

        override fun getItemCount(): Int {
            return daftarPresensi.size
        }

}