package com.threelab.apsensi.data

data class AttendanceItem(
    val date: String,
    val time_start: String,
    val time_end: String,
    val status: String?,
    val information: String?,
    val subject: String
)