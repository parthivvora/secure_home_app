package com.example.securehome

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.securehome.helperClass.SharedPreferencesManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var signInBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        email = findViewById(R.id.loginEmail)
        password = findViewById(R.id.loginPassword)
        signInBtn = findViewById(R.id.signInBtn)

        signInBtn.setOnClickListener {
            loginUser()
        }
    }

    override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            Log.d("ZZZ", "onStart: " + auth.currentUser)
            Toast.makeText(this, "Logout ...!", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("ZZZ", "onStart: " + auth.currentUser!!.uid)
        }
    }

    private fun loginUser() {
        val email = email.text.toString()
        val password = password.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please, Enter email and password ...!", Toast.LENGTH_SHORT).show()
        } else {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val id = auth.currentUser?.uid ?: ""
                        Log.d("id", "User id = $id")
                        val sharedPreferencesManager=SharedPreferencesManager(this)
                        sharedPreferencesManager.saveUserId(id)
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    fun navigateRegister(view: View) {
        startActivity(Intent(this, RegisterActivity::class.java))
        finish()
    }
}