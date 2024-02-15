@file:Suppress("DEPRECATION")

package com.example.securehome

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.securehome.dataModel.UserDataModel
import com.example.securehome.databinding.ActivityProfileBinding
import com.example.securehome.helperClass.SharedPreferencesManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

@Suppress("DEPRECATION")
class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var uri: Uri
    private val database = FirebaseDatabase.getInstance().reference
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val sharedPreferencesManager = SharedPreferencesManager(this)
        val userId = sharedPreferencesManager.getUserId()

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading Image...")
        progressDialog.setCancelable(false)

        binding.updateProfileBtn.setOnClickListener {
            updateUserProfile(userId)
        }

        getUserProfileData(userId)

        val galleryImage = registerForActivityResult(ActivityResultContracts.GetContent()
        ) {
            binding.userProfileImage.setImageURI(it)
            uri = it!!
        }

        binding.select.setOnClickListener {
            galleryImage.launch("image/*")
        }

        binding.upload.setOnClickListener {
            uploadImage()
        }
    }

    // Upload image in Firebase Storage
    private fun uploadImage() {
        progressDialog.show()
        val imageName = "profile_image_${System.currentTimeMillis()}.jpg"
        val storageReference: StorageReference =
            FirebaseStorage.getInstance().reference.child("user/${imageName}")
        storageReference
            .putFile(uri)
            .addOnSuccessListener { task ->
                task.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        val userId = FirebaseAuth.getInstance().currentUser!!.uid
                        updateProfileInDatabase(userId, imageUrl)
                        progressDialog.dismiss()
                    }
                    .addOnFailureListener { error ->
                        progressDialog.dismiss()
                        Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { error ->
                progressDialog.dismiss()
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }
    }

    // Update image in Real time database using userId
    private fun updateProfileInDatabase(userId: String, imageUrl: String) {
        val userRef = database.child("user").child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userProfile = snapshot.getValue(UserDataModel::class.java)
                val updatedProfile = userProfile?.copy(image = imageUrl)
                userRef.setValue(updatedProfile)
                Toast.makeText(
                    this@ProfileActivity,
                    "Your profile image is uploaded...!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("updateProfileInDatabase error", "onCancelled: ${error.message}")
            }
        })
    }

    // Update user profile data
    private fun updateUserProfile(id: String?) {
        val name = binding.updateName.text.toString()
        val contact = binding.updateContact.text.toString()

        val database = FirebaseDatabase.getInstance().getReference("user").child(id.toString())
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val existingUserData = snapshot.getValue(UserDataModel::class.java)
                    existingUserData.let {
                        it?.name = name
                        it?.contact = contact
                    }
                    database.setValue(existingUserData)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this@ProfileActivity,
                                    "User data updated successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.updateName.clearFocus()
                                binding.updateContact.clearFocus()
                            } else {
                                Toast.makeText(
                                    this@ProfileActivity,
                                    "Failed to update user data",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(this@ProfileActivity, "User not found", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ProfileActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    // Get User profile data
    private fun getUserProfileData(id: String?) {
        val database = FirebaseDatabase.getInstance().getReference("user").child(id.toString())
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserDataModel::class.java)
                    if (user != null) {
                        binding.updateName.setText(user.name)
                        binding.updateContact.setText(user.contact)
                        if (!user.image.isNullOrEmpty()) {
                            Glide.with(this@ProfileActivity)
                                .load(user.image)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(binding.userProfileImage)
                        } else {
                            binding.userProfileImage.setImageResource(R.drawable.user_profile_avatar_black)
                        }
                    } else {
                        Toast.makeText(
                            this@ProfileActivity, "User data is not found", Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this@ProfileActivity, "User not found", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ProfileActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
    }
}