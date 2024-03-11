package com.example.securehome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.securehome.dataModel.UserDataModel
import com.example.securehome.databinding.ActivityMainBinding
import com.example.securehome.helperClass.SharedPreferencesManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var homeBinding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeBinding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)

        homeBinding.menu.setOnClickListener {
            homeBinding.container.openDrawer(GravityCompat.START)
            getUserData(userId)
        }

        homeBinding.customMenu.visitor.setOnClickListener {
            startActivity(Intent(this@MainActivity, VisitorActivity::class.java))
        }
        homeBinding.customMenu.profile.setOnClickListener {
            startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
        }
        homeBinding.customMenu.history.setOnClickListener {
            startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
        }
        homeBinding.customMenu.notification.setOnClickListener {
            startActivity(Intent(this@MainActivity, NotificationActivity::class.java))
        }
        homeBinding.customMenu.privacyPolicy.setOnClickListener {
            startActivity(Intent(this@MainActivity, PrivacyPolicyActivity::class.java))
        }
        homeBinding.customMenu.termsCondition.setOnClickListener {
            startActivity(Intent(this@MainActivity, TermsConditionActivity::class.java))
        }
        homeBinding.customMenu.logout.setOnClickListener {
            auth.signOut()
            val sharedPreferencesManager = SharedPreferencesManager(this@MainActivity)
            sharedPreferencesManager.clearUserId()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }

        homeBinding.visitorCard.setOnClickListener {
            startActivity(Intent(this@MainActivity, VisitorActivity::class.java))
        }
        homeBinding.historyCard.setOnClickListener {
            startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        } else {
            Log.d("ZZZ", "onStart: " + auth.currentUser!!.uid)
            userId = auth.currentUser!!.uid
        }
    }

    // Get user data and show into navigation drawer (Header Layout)
    private fun getUserData(userId: String) {
        val database = FirebaseDatabase.getInstance().getReference("user").child(userId)
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserDataModel::class.java)
                    if (user != null) {
                        val userName = user.name
                        val userContact = user.contact
                        homeBinding.customMenu.userName.text = userName
                        homeBinding.customMenu.userContact.text = userContact
                        if (!user.image.isNullOrEmpty()) {
                            Glide.with(this@MainActivity)
                                .load(user.image)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(homeBinding.customMenu.userImage)
                        } else {
                            homeBinding.customMenu.userImage.setImageResource(R.drawable.user_profile_avatar)
                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity, "User data is not found", Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "User not found", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@MainActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (homeBinding.container.isDrawerOpen(GravityCompat.START)) {
            homeBinding.container.close()
        } else {
            super.onBackPressed()
            finishAffinity()
        }
    }

    override fun onResume() {
        super.onResume()
        if (homeBinding.container.isDrawerOpen(GravityCompat.START)) {
            homeBinding.container.close()
        }
    }
}

// 11/03/2024 14/02/2024 02/02/2024 16/02/2024