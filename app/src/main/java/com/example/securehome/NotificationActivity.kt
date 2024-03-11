package com.example.securehome

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.securehome.databinding.ActivityNotificationBinding

@Suppress("DEPRECATION")
class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this@NotificationActivity,
            R.layout.activity_notification
        )

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this@NotificationActivity, MainActivity::class.java))
            finish()
        }

        try {
            val title = intent.extras?.getString("title")
            val body = intent.extras?.getString("body")
            if (title.isNullOrEmpty() && body.isNullOrEmpty()) {
                binding.noNotificationText.text = "No notification here..!"
//                binding.notificationTitle.text = "Notification title"
//                binding.notificationDescription.text = "Notification description"
            } else {
                binding.notificationTitle.text = title
                binding.notificationDescription.text = body
            }
        } catch (e: Exception) {
            Log.d("messageId error", "onCreate: ${e.message}")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@NotificationActivity, MainActivity::class.java))
        finish()
    }
}