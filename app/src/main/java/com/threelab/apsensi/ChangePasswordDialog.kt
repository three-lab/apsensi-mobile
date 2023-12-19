package com.threelab.apsensi

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.threelab.apsensi.Helper.Constant
import com.threelab.apsensi.Helper.PreferencesHelper
import com.threelab.apsensi.data.SessionData
import org.json.JSONObject

class ChangePasswordDialog(context: Context): AppCompatDialog(context) {
    private lateinit var oldPassword: EditText
    private lateinit var newPassword: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var changePasswordBtn: Button
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var requestQueue: RequestQueue
    private lateinit var sharedPref: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.dialog_changepassword)

        oldPassword = findViewById(R.id.oldPassword)!!
        newPassword = findViewById(R.id.newPassword)!!
        confirmPassword = findViewById(R.id.confirmPassword)!!
        changePasswordBtn = findViewById(R.id.buttonResetPassword)!!

        loadingDialog = LoadingDialog(context)
        requestQueue = Volley.newRequestQueue(context)
        sharedPref = PreferencesHelper(context)

        val layoutParams = WindowManager.LayoutParams()

        layoutParams.copyFrom(window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT

        window?.attributes = layoutParams
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        changePasswordBtn.setOnClickListener { validateInput() }
    }

    private fun validateInput() {
        val newPasswordText = newPassword.text.toString()
        val confirmPasswordText = confirmPassword.text.toString()

        when {
            oldPassword.text.toString().isEmpty() -> {
                showToast("Password lama harus diisi")
            }

            newPasswordText.isEmpty() -> {
                showToast("Password baru harus diisi")
            }

            newPasswordText.length < 8 -> {
                showToast("Password minimal 8 karakter")
            }

            !confirmPasswordText.equals(newPasswordText) -> {
                showToast("Konfirmasi password tidak sama")
            }

            else -> {
                resetPassword()
            }
        }
    }

    private fun resetPassword() {
        loadingDialog.showLoading()

        val endpoint = Constant.API_ENDPOINT + "/change-pass"
        val dataRequest = JSONObject()
        val headers = hashMapOf<String, String>(
            "Authorization" to sharedPref.getString(Constant.PREF_TOKEN).toString()
        )

        dataRequest.put("old_password", oldPassword.text.toString())
        dataRequest.put("new_password", newPassword.text.toString())
        dataRequest.put("confirm_password", confirmPassword.text.toString())

        val request = object: JsonObjectRequest(Request.Method.POST, endpoint, dataRequest,
            { response ->
                loadingDialog.hideLoading()
                showToast("Berhasil mengganti password")
                this.hide()
            },
            { error ->
                val response = JSONObject(String(error.networkResponse?.data ?: ByteArray(0)))
                val message = response.getJSONObject("meta").getString("message")

                loadingDialog.hideLoading()
                showToast(message)
            }) {

            override fun getHeaders(): MutableMap<String, String> {
                return headers
            }
        }

        requestQueue.add(request)
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}