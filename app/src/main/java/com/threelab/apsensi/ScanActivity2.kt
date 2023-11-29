package com.threelab.apsensi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.SparseIntArray
import android.view.Surface
import android.view.TextureView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.threelab.apsensi.Helper.Constant
import java.nio.ByteBuffer
import java.util.*
import kotlin.math.max

class ScanActivity2 : AppCompatActivity() {

    private lateinit var textureView: TextureView
    private lateinit var switchCameraButton: Button
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraDevice: CameraDevice
    private lateinit var captureRequestBuilder: CaptureRequest.Builder
    private lateinit var cameraCaptureSessions: CameraCaptureSession
    private lateinit var backgroundHandler: Handler
    private lateinit var backgroundThread: HandlerThread
    private lateinit var imageReader: ImageReader
    private var cameraId: String = ""
    private var currentCameraLensFacing = CameraCharacteristics.LENS_FACING_BACK
    private lateinit var requestQueue: RequestQueue

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 200
        private val ORIENTATIONS = SparseIntArray()

        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 180)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 360)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan2)
        supportActionBar?.hide()

        textureView = findViewById(R.id.textureView)
        switchCameraButton = findViewById(R.id.switchCamera)
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        requestQueue = Volley.newRequestQueue(this@ScanActivity2)

        switchCameraButton.setOnClickListener {
            switchCamera()
        }

        // Tombol kirim yang digunakan untuk mengambil foto
        val kirim: Button = findViewById(R.id.kirim)
        kirim.setOnClickListener {
            takePicture()
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
            return
        }

        openCamera()
    }

    private fun configureTransform(viewWidth: Int, viewHeight: Int) {
        val rotation = windowManager.defaultDisplay.rotation
        val matrix = Matrix()
        val viewRect = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()

        when (rotation) {
            Surface.ROTATION_90, Surface.ROTATION_270 -> {
                viewRect.offset(centerX - viewRect.centerX(), centerY - viewRect.centerY())
                matrix.setRectToRect(
                    viewRect,
                    RectF(0f, 0f, viewHeight.toFloat(), viewWidth.toFloat()),
                    Matrix.ScaleToFit.FILL
                )
                val scale = max(
                    viewHeight.toFloat() / viewRect.width(),
                    viewWidth.toFloat() / viewRect.height()
                )
                matrix.postScale(scale, scale, centerX, centerY)
                matrix.postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)
            }
            Surface.ROTATION_180 -> matrix.postRotate(180f, centerX, centerY)
        }

        textureView.setTransform(matrix)
    }

    private val surfaceTextureListener: TextureView.SurfaceTextureListener =
        object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                configureTransform(width, height)
                openCamera()
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
                configureTransform(width, height)
                closeCamera()
                openCamera()
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
        }

    private fun takePicture() {
        // Check if cameraDevice is null or not properly initialized
        if (cameraDevice == null || !::imageReader.isInitialized) {
            Toast.makeText(this, "Error taking picture. Please try again.", Toast.LENGTH_SHORT).show()
            return
        }

        val captureCallback = object : CameraCaptureSession.CaptureCallback() {
            override fun onCaptureCompleted(
                session: CameraCaptureSession,
                request: CaptureRequest,
                result: TotalCaptureResult
            ) {
                super.onCaptureCompleted(session, request, result)

                // Use the image data from the result
                val image = result.get(CaptureResult.SENSOR_TIMESTAMP)
                if (image != null) {
                    sendImageToServer(image)
                }
            }
        }

        try {
            val captureBuilder =
                cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureBuilder.addTarget(imageReader.surface)

            val rotation = windowManager.defaultDisplay.rotation
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation))

            val captureCallbackHandler = Handler(backgroundThread.looper)
            cameraCaptureSessions.stopRepeating()
            cameraCaptureSessions.capture(
                captureBuilder.build(),
                captureCallback,
                captureCallbackHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun sendImageToServer(imageTimestamp: Long) {
        val imageData: ByteArray = convertTimestampToByteArray(imageTimestamp)

        val url = Constant.API_ENDPOINT + "/attendance/attempt"
        val stringRequest = object : StringRequest(
            Request.Method.POST,
            url,
            { response ->
                // Handle the server response
                Toast.makeText(this, "Image sent successfully", Toast.LENGTH_SHORT).show()
            },
            { error ->
                // Handle errors
                Toast.makeText(this, "Error sending image: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getBodyContentType(): String {
                return "application/octet-stream"
            }

            override fun getBody(): ByteArray {
                return imageData
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun convertTimestampToByteArray(timestamp: Long): ByteArray {
        return timestamp.toString().toByteArray()
    }


    private fun switchCamera() {
        currentCameraLensFacing =
            if (currentCameraLensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                CameraCharacteristics.LENS_FACING_FRONT
            } else {
                CameraCharacteristics.LENS_FACING_BACK
            }

        closeCamera()
        openCamera()
    }

    private fun openCamera() {
        val cameraIdList = cameraManager.cameraIdList
        for (id in cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(id)
            if (characteristics.get(CameraCharacteristics.LENS_FACING) == currentCameraLensFacing) {
                cameraId = id
            }
        }

        val stateCallback = object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera
                createCameraPreview()
            }

            override fun onDisconnected(camera: CameraDevice) {
                cameraDevice.close()
            }

            override fun onError(camera: CameraDevice, error: Int) {
                cameraDevice.close()
            }
        }

        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
                return
            }
            cameraManager.openCamera(cameraId, stateCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun createCameraPreview() {
        val texture = textureView.surfaceTexture
        if (texture != null) {
            texture.setDefaultBufferSize(textureView.width, textureView.height)
        }
        val surface = Surface(texture)

        try {
            captureRequestBuilder =
                cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(surface)

            cameraDevice.createCaptureSession(
                listOf(surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        if (cameraDevice == null) return
                        cameraCaptureSessions = session

                        captureRequestBuilder.set(
                            CaptureRequest.CONTROL_MODE,
                            CameraMetadata.CONTROL_MODE_AUTO
                        )

                        updatePreview()
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {}
                }, null
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun updatePreview() {
        captureRequestBuilder.set(
            CaptureRequest.CONTROL_MODE,
            CameraMetadata.CONTROL_MODE_AUTO
        )
        val rotation = windowManager.defaultDisplay.rotation
        captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation))
        try {
            cameraCaptureSessions.setRepeatingRequest(
                captureRequestBuilder.build(),
                null,
                backgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun closeCamera() {
        if (cameraDevice != null) {
            cameraDevice.close()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        if (textureView.isAvailable) {
            openCamera()
        } else {
            textureView.surfaceTextureListener = surfaceTextureListener
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
            return
        }
    }

    override fun onPause() {
        closeCamera()
        stopBackgroundThread()
        super.onPause()
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("CameraBackground")
        backgroundThread.start()
        backgroundHandler = Handler(backgroundThread.looper)
    }

    private fun stopBackgroundThread() {
        backgroundThread.quitSafely()
        try {
            backgroundThread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}


