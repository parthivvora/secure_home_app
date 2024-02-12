package com.example.securehome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.securehome.databinding.ActivityNotificationBinding

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this@NotificationActivity,
            R.layout.activity_notification
        )
    }
}