package com.threelab.apsensi.Helper

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.InputStream

class FileHelper {
    fun bitmapToByteArray(bitmap: Bitmap?): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    fun pdfToByteArray(context: Context, pdfUri: Uri): ByteArray? {
        val contentResolver: ContentResolver = context.contentResolver
        val pdfInputStream: InputStream? = contentResolver.openInputStream(pdfUri)

        return try {
            pdfInputStream?.use {
                val byteArrayOutputStream = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var bytesRead: Int

                while ((it.read(buffer)).also { bytesRead = it } != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead)
                }

                byteArrayOutputStream.toByteArray()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            pdfInputStream?.close()
        }
    }
}