package com.example.securehome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.securehome.databinding.ActivityPrivacyPolicyBinding

@Suppress("DEPRECATION")
class PrivacyPolicyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrivacyPolicyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@PrivacyPolicyActivity, R.layout.activity_privacy_policy)

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this@PrivacyPolicyActivity, MainActivity::class.java))
            finish()
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@PrivacyPolicyActivity, MainActivity::class.java))
        finish()
    }
}