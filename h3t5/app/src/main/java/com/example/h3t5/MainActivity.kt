package com.example.h3t5

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var cameraButton: Button
    private lateinit var galleryButton: Button

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                val imageUriString = saveBitmapToFile(imageBitmap)
                openDisplayImageActivity(imageUriString)
            }
        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri = result.data?.data
                openDisplayImageActivity(uri.toString())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraButton = findViewById(R.id.cameraButton)
        galleryButton = findViewById(R.id.galleryButton)

        cameraButton.setOnClickListener {
            // 在点击相机按钮时检查并请求相机权限
            checkCameraPermission()
        }

        galleryButton.setOnClickListener {
            dispatchPickImageIntent()
        }
    }

    private fun checkCameraPermission() {
        // 检查相机权限是否已授予
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // 如果已授予相机权限，则执行拍照操作
            dispatchTakePictureIntent()
        } else {
            // 如果未授予相机权限，则请求权限
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        // 请求相机权限
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 如果用户授予了相机权限，则执行拍照操作
                dispatchTakePictureIntent()
            } else {
                // 如果用户拒绝了相机权限请求，则显示一条消息或采取其他适当的措施
                // 这里您可以根据需要添加逻辑
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureLauncher.launch(takePictureIntent)
    }

    private fun dispatchPickImageIntent() {
        val pickImageIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageIntent.type = "image/*"
        pickImageLauncher.launch(Intent.createChooser(pickImageIntent, "Select Picture"))
    }

    private fun saveBitmapToFile(bitmap: Bitmap?): String? {
        if (bitmap == null) return null

        val file = File(cacheDir, "captured_image.jpg")
        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return Uri.fromFile(file).toString()
    }

    private fun openDisplayImageActivity(imageUriString: String?) {
        val intent = Intent(this, DisplayImageActivity::class.java)
        intent.putExtra("imageUri", imageUriString)
        startActivity(intent)
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 101
    }
}
