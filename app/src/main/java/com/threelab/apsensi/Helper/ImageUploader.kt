package com.threelab.apsensi.Helper

import android.content.Context
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.util.*
import kotlin.collections.HashMap

class ImageUploader(private val context: Context) {

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun uploadImage(
        url: String,
        authToken: String,
        imageByteArray: ByteArray,
        listener: Response.Listener<String>,
        errorListener: Response.ErrorListener
    ) {
        val boundary = "----WebKitFormBoundary${UUID.randomUUID().toString()}"
        val twoHyphens = "--"
        val lineEnd = "\r\n"

        try {
            val dataOutputStream = ByteArrayOutputStream()
            val writer = DataOutputStream(dataOutputStream)

            // Start of the request
            writer.writeBytes(twoHyphens + boundary + lineEnd)
            writer.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\"image.jpg\"$lineEnd")
            writer.writeBytes("Content-Type: image/jpeg$lineEnd$lineEnd")

            // Image data
            writer.write(imageByteArray)
            writer.writeBytes(lineEnd)

            // End of the request
            writer.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)

            val requestBody = dataOutputStream.toByteArray()

            val stringRequest = object : StringRequest(
                Request.Method.POST, url,
                Response.Listener<String> { response ->
                    // Handle the response
                    listener.onResponse(response)
                },
                errorListener
            ) {
                override fun getBodyContentType(): String {
                    return "multipart/form-data; boundary=$boundary"
                }

                override fun getBody(): ByteArray {
                    return requestBody
                }

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = authToken

                    return headers
                }

                @Throws(UnsupportedEncodingException::class)
                override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
                    val parsed: String = String(response.data, charset(HttpHeaderParser.parseCharset(response.headers)))
                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response))
                }
            }

            requestQueue.add(stringRequest)

        } catch (e: IOException) {
            e.printStackTrace()
            // Handle the exception
            val volleyError = VolleyError(e.message)
            errorListener.onErrorResponse(volleyError)
        }
    }
}
