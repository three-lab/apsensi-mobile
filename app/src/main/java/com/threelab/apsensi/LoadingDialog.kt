package com.threelab.apsensi

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialog
import com.airbnb.lottie.LottieAnimationView

class LoadingDialog(context: Context): AppCompatDialog(context) {
    private lateinit var lottieAnimation: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)

        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setCancelable(false)
    }

    fun showLoading() {
        show()
    }

    fun hideLoading() {
        dismiss()
    }
}