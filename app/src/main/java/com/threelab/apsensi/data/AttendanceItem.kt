package com.threelab.apsensi.data

data class AttendanceItem(
    val date: String,
    val timeStart: String,
    val timeEnd: String,
    val status: String?,
    val information: String?,
    val subject: String
)