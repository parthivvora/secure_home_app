package com.example.securehome

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.example.securehome.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var homeBinding: ActivityMainBinding;
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        homeBinding.menu.setOnClickListener {
            homeBinding.container.openDrawer(GravityCompat.START)
        }

        homeBinding.customMenu.visitor.setOnClickListener {
            startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
        }
        homeBinding.customMenu.profile.setOnClickListener {
            startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
        }
        homeBinding.customMenu.notification.setOnClickListener {
            startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
        }
        homeBinding.customMenu.privacyPolicy.setOnClickListener {
            startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
        }
        homeBinding.customMenu.termsCondition.setOnClickListener {
            startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
        }
        homeBinding.customMenu.logout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }
//        homeBinding.customMenu.cross.setOnClickListener {
//            homeBinding.container.close()
//        }
    }

    override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            Log.d("ZZZ", "onStart: " + auth.currentUser!!.uid)
        }
    }

    override fun onBackPressed() {
        if (homeBinding.container.isDrawerOpen(GravityCompat.START)) {
            homeBinding.container.close()
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        if (homeBinding.container.isDrawerOpen(GravityCompat.START)) {
            homeBinding.container.close()
        }
    }
}