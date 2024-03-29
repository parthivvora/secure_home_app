package com.example.securehome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.securehome.dataModel.UserDataModel
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var name: TextInputEditText
    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var contact: TextInputEditText
    private lateinit var flatNo: TextInputEditText
    private lateinit var signupBtn: Button
    private lateinit var signInBtn: TextView
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var buildingNo: String
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        name = findViewById(R.id.userName)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        contact = findViewById(R.id.contact)
        flatNo = findViewById(R.id.flatNo)
        signupBtn = findViewById(R.id.signupBtn)
        signInBtn = findViewById(R.id.signInBtn)
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView)

        // Select building no using Dropdown menu
        val languages = resources.getStringArray(R.array.building_name)
        val arrayAdapter = ArrayAdapter(this, R.layout.building_no_dropdown, languages)
        autoCompleteTextView.setAdapter(arrayAdapter)
        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            buildingNo = parent.getItemAtPosition(position).toString()
        }

        signupBtn.setOnClickListener {
            registerUser()
        }

        signInBtn.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser() {
        val name = name.text.toString()
        val email = email.text.toString()
        val password = password.text.toString()
        val contact = contact.text.toString()
        val flatNo = flatNo.text.toString()

        if (name.isEmpty() || contact.isEmpty() || buildingNo.isEmpty() || flatNo.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this@RegisterActivity, "Please, Enter all details ...!", Toast.LENGTH_SHORT).show()
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val id = auth.currentUser?.uid ?: ""
                        saveUserData(id, name, email, password, contact, buildingNo, flatNo)
                    }
                }
                .addOnFailureListener { err ->
                    Toast.makeText(
                        this@RegisterActivity,
                        err.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun saveUserData(
        id: String,
        name: String,
        email: String,
        password: String,
        contact: String,
        buildingNo: String,
        flatNo: String
    ) {
        try {
            val dbRef = FirebaseDatabase.getInstance().getReference("user")
            val user = UserDataModel(
                id,
                name,
                email,
                password,
                contact,
                buildingNo,
                flatNo
            )
            dbRef.child(id).setValue(user)
                .addOnCompleteListener {
                    Toast.makeText(this@RegisterActivity, "You are successfully registered...!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                }
                .addOnFailureListener { err ->
                    Toast.makeText(this@RegisterActivity, "Register failed ${err.message}", Toast.LENGTH_LONG).show()
                    Log.d("Register user error in function", "saveUserData: ${err.message}")
                }
        }
        catch (e:Exception){
            Log.d("Register user error", "saveUserData: ${e.message}")
        }
    }
}