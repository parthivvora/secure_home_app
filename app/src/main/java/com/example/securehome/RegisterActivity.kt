package com.example.securehome

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
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
    private lateinit var buildingNo: TextInputEditText
    private lateinit var flatNo: TextInputEditText
    private lateinit var signupBtn: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

//        val image= intent.extras?.getInt("image")
//        val date= intent.extras?.getString("date")
//        val inTime= intent.extras?.getString("inTime")
//        val outTime= intent.extras?.getString("outTime")
//
//        Log.d("getDataFromIntent", "image: $image")
//        Log.d("getDataFromIntent", "date: $date")
//        Log.d("getDataFromIntent", "inTime: $inTime")
//        Log.d("getDataFromIntent", "outTime: $outTime")

        auth = FirebaseAuth.getInstance()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        name = findViewById(R.id.userName)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        contact = findViewById(R.id.contact)
        buildingNo = findViewById(R.id.buildingName)
        flatNo = findViewById(R.id.flatNo)
        signupBtn = findViewById(R.id.signupBtn)

        signupBtn.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val name = name.text.toString()
        val email = email.text.toString()
        val password = password.text.toString()
        val contact = contact.text.toString()
        val buildingNo = buildingNo.text.toString()
        val flatNo = flatNo.text.toString()

        if (name.isEmpty() || contact.isEmpty() || buildingNo.isEmpty() || flatNo.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please, Enter all details ...!", Toast.LENGTH_SHORT).show()
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
                        this,
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
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("user")
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
                Toast.makeText(this, "Data Inserted...!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, LoginActivity::class.java))
            }
            .addOnFailureListener { err ->
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
            }
    }

    fun navigateLogin(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}