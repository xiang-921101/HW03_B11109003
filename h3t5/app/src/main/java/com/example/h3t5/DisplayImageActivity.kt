package com.example.h3t5

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class DisplayImageActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var rotateButton: Button
    private lateinit var backButton: Button

    private lateinit var imageUri: Uri
    private var rotatedBitmap: Bitmap? = null // 保存旋转后的图像

    private val CHANNEL_ID = "rotate_notification_channel"
    private val NOTIFICATION_ID = 123
    private val REQUEST_CODE_PERMISSION_NOTIFICATION = 456

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)

        imageView = findViewById(R.id.imageView)
        rotateButton = findViewById(R.id.rotateButton)
        backButton = findViewById(R.id.backButton)

        // 接收从 MainActivity 传递过来的图片 URI
        imageUri = Uri.parse(intent.getStringExtra("imageUri"))

        // 显示图片
        showImage()

        // 检查并请求通知权限
        checkAndRequestNotificationPermission()

        rotateButton.setOnClickListener {
            // 旋转图片
            rotateImage()
        }

        backButton.setOnClickListener {
            // 返回上一页面
            finish()
        }
    }

    private fun showImage() {
        try {
            // 从 URI 中读取图片并设置给 ImageView
            val inputStream = contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(bitmap)
            inputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun rotateImage() {
        try {
            // 从 URI 中读取图片并设置给 ImageView
            val inputStream = contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // 如果已经进行过旋转，则基于旋转后的图像再次旋转
            val sourceBitmap = rotatedBitmap ?: bitmap
            val matrix = Matrix().apply { postRotate(90f) }
            rotatedBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.width, sourceBitmap.height, matrix, true)
            imageView.setImageBitmap(rotatedBitmap)
            inputStream?.close()

            // 发送通知
            sendNotification()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendNotification() {
        // 检查通知权限是否已授予
        if (checkNotificationPermission()) {
            // 创建通知渠道（仅适用于 Android O 及更高版本）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Rotate Notification Channel"
                val descriptionText = "Notification Channel for Rotate Image"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }

            // 创建通知
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Image Rotated")
                .setContentText("Your image has been rotated successfully")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(this)) {
                // 显示通知
                notify(NOTIFICATION_ID, builder.build())
            }
        }
    }

    private fun checkAndRequestNotificationPermission() {
        // 检查通知权限是否已授予
        if (!checkNotificationPermission()) {
            // 如果未授予，请求权限
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECEIVE_BOOT_COMPLETED),
                REQUEST_CODE_PERMISSION_NOTIFICATION
            )
        }
    }

    private fun checkNotificationPermission(): Boolean {
        // 检查通知权限是否已授予
        return NotificationManagerCompat.from(this).areNotificationsEnabled()
    }
}
