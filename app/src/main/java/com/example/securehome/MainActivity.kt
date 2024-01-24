package com.example.securehome

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import com.example.securehome.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var container: DrawerLayout
    lateinit var menu: ImageView
    lateinit var homeBinding: ActivityMainBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

//        container=findViewById(R.id.container)
//        menu=findViewById(R.id.menu)

        homeBinding.menu.setOnClickListener {
            homeBinding.container.openDrawer(GravityCompat.START)
        }

        homeBinding.customMenu.visitor.setOnClickListener {
        startActivity(Intent(this@MainActivity,RegisterActivity::class.java))
        }
        homeBinding.customMenu.cross.setOnClickListener {
            homeBinding.container.close()
        }
        homeBinding.customMenu.History.setOnClickListener {
            startActivity(Intent(this@MainActivity,LoginActivity::class.java))

        }
    }

    override fun onBackPressed() {

        if (homeBinding.container.isDrawerOpen(GravityCompat.START)) {
            homeBinding.container.close()
        }
        else
        {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        if (homeBinding.container.isDrawerOpen(GravityCompat.START)) {
            homeBinding.container.close()
        }
    }
//    private fun setUpViews() {
//        val appBar: Toolbar = findViewById(R.id.appBar)
//        val navigationDrawer: DrawerLayout = findViewById(R.id.navigation_drawer)
//        navigationView = findViewById(R.id.navigationView)
//        setSupportActionBar(appBar)
//
//        actionBarDrawerToggle =
//            ActionBarDrawerToggle(this, navigationDrawer, R.string.open, R.string.close)
//        actionBarDrawerToggle.syncState()
//
//        navigationView.setNavigationItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.visitor -> {
//                    Log.e("==========", "setUpViews: " )
//                    startActivity(Intent(this,RegisterActivity::class.java))
//                }
//                R.id.history -> Toast.makeText(this, "History", Toast.LENGTH_SHORT).show()
//                R.id.notification -> Toast.makeText(this, "Notification", Toast.LENGTH_SHORT)
//                    .show()
//
//                R.id.privacy_policy -> Toast.makeText(
//                    this,
//                    "Privacy Policy",
//                    Toast.LENGTH_SHORT
//                ).show()
//
//                R.id.terms_and_condition -> Toast.makeText(
//                    this,
//                    "Terms & Condition",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//            true
//        }
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
//            return true
//        }
//        return super.onOptionsItemSelected(item)
//    }
}