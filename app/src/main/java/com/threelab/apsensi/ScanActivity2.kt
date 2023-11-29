package com.threelab.apsensi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.Surface
import android.view.TextureView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.threelab.apsensi.Helper.Constant
import com.threelab.apsensi.Helper.ImageUploader
import com.threelab.apsensi.Helper.PreferencesHelper
import java.nio.ByteBuffer

class ScanActivity2 : AppCompatActivity() {

    private lateinit var textureView: TextureView
    private lateinit var switchCameraButton: Button
    private lateinit var kirimButton: Button
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraDevice: CameraDevice
    private lateinit var captureRequestBuilder: CaptureRequest.Builder
    private lateinit var cameraCaptureSessions: CameraCaptureSession
    private lateinit var backgroundHandler: Handler
    private lateinit var backgroundThread: HandlerThread
    private lateinit var imageReader: ImageReader
    private lateinit var imageUploader: ImageUploader
    private lateinit var sharedpref: PreferencesHelper

    private var cameraId: String = ""
    private var currentCameraLensFacing = CameraCharacteristics.LENS_FACING_BACK

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

        textureView = findViewById(R.id.textureView)
        switchCameraButton = findViewById(R.id.switchCamera)
        kirimButton = findViewById(R.id.kirim)
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        sharedpref = PreferencesHelper(this@ScanActivity2)
        imageUploader = ImageUploader(this)

        switchCameraButton.setOnClickListener {
            switchCamera()
        }

        kirimButton.setOnClickListener {
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

        // Initialize ImageReader
        val imageDimension = Size(640, 480) // Adjust the size as needed
        imageReader = ImageReader.newInstance(
            imageDimension.width,
            imageDimension.height,
            ImageFormat.JPEG,
            1
        )

        openCamera()
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

    private fun takePicture() {
        if (cameraDevice == null) return

        val captureCallback = object : CameraCaptureSession.CaptureCallback() {
            override fun onCaptureCompleted(
                session: CameraCaptureSession,
                request: CaptureRequest,
                result: TotalCaptureResult
            ) {
                super.onCaptureCompleted(session, request, result)

                // Get the image data from the result
                val image = result.get(CaptureResult.SENSOR_TIMESTAMP) as Long

                // Convert the timestamp to a ByteArray
                val imageData = convertTimestampToByteArray(image)

                // Send the image data to the server
                sendImageToServer(imageData)
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

    private fun sendImageToServer(imageData: ByteArray) {
        val url = Constant.API_ENDPOINT + "/attendances/attempt"
        val token = sharedpref.getString(Constant.PREF_TOKEN) ?: ""

        imageUploader.uploadImage(
            url,
            token,
            imageData,
            {response ->
                Toast.makeText(this, response.toString(), Toast.LENGTH_LONG).show()
            },
            { error ->
                Toast.makeText(this, String(error.networkResponse.data), Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun convertTimestampToByteArray(timestamp: Long): ByteArray {
        // You need to implement the logic to convert the timestamp to a byte array
        // This is just a placeholder; you should adapt it based on your needs
        // For example, you might use ByteBuffer, convert to string, or use another encoding
        val byteBuffer = ByteBuffer.allocate(java.lang.Long.BYTES)
        byteBuffer.putLong(timestamp)
        return byteBuffer.array()
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
                listOf(surface, imageReader.surface),
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
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                finish()
            } else {
                openCamera()
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

    private val surfaceTextureListener: TextureView.SurfaceTextureListener =
        object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                openCamera()
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                closeCamera()
                openCamera()
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
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
