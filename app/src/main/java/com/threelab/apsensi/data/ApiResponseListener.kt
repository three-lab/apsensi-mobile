package com.threelab.apsensi.data

interface ApiResponseListener {
    fun onSuccess(data: List<Schedule>)

    // Called when there is an error in the API request
    fun onError(errorMessage: String)
}
