package com.example.securehome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.securehome.databinding.ActivityTermsConditionBinding

class TermsConditionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTermsConditionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this@TermsConditionActivity,
            R.layout.activity_terms_condition
        )

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this@TermsConditionActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@TermsConditionActivity, MainActivity::class.java))
        finish()
    }
}