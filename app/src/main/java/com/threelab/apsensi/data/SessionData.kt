package com.threelab.apsensi.data

import com.google.gson.Gson

class SessionData {
    companion object {
        private var employee: Employee? = null

        fun saveEmployee(jsonString: String) {
            val gson = Gson()
            employee = gson.fromJson(jsonString, Employee::class.java)
        }

        fun getEmployee(): Employee? {
            return employee
        }
    }
}

data class Employee(
    val id: Int,
    val fullname: String,
    val birthdate: String,
    val birthplace: String,
    val photos: EmployeePhotos,
    val username: String,
    val email: String,
    val nik: String,
    val address: String,
    val gender: String
)

data class EmployeePhotos(
    val front: String,
    val left: String,
    val right: String
)