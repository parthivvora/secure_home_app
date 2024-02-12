package com.example.securehome

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.securehome.dataModel.UserDataModel
import com.example.securehome.dataModel.VisitorUserData
import com.example.securehome.databinding.ActivityHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var visitorUserList: ArrayList<VisitorUserData>
    private lateinit var adapter: VisitorDataAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private lateinit var selectedDate: String
    private lateinit var progressDialog: ProgressDialog

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@HistoryActivity, R.layout.activity_history)

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this@HistoryActivity, MainActivity::class.java))
            finish()
        }

        visitorUserList = arrayListOf<VisitorUserData>()
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            startActivity(Intent(this@HistoryActivity, LoginActivity::class.java))
            finish()
        } else {
            Log.d("HistoryActivity user id", "onStart: " + auth.currentUser!!.uid)
            userId = auth.currentUser!!.uid
            getVisitorData(userId)
        }

//        binding.historyDatePicker.setOnDateChangedListener { _, year, month, date ->
//            val selectedMonth = month + 1
//            selectedDate = "$date/$selectedMonth/$year"
//            Log.d("Date select function", "onCreate: $selectedDate")
//        }

        progressDialog = ProgressDialog(this@HistoryActivity)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun getVisitorData(userId: String) {
        val usersRef = FirebaseDatabase.getInstance().getReference("user").child(userId)
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(UserDataModel::class.java)
                user?.let {
                    val userNumber = user.contact
                    userNumber?.let {
                        fetchVisitors(userNumber)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@HistoryActivity,
                    "Database error: ${databaseError.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun fetchVisitors(userNumber: String) {
        val visitorsRef = FirebaseDatabase.getInstance().getReference("visitor")
        visitorsRef.orderByChild("mobile").equalTo(userNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    progressDialog.dismiss()
                    for (visitorSnapshot in dataSnapshot.children) {
                        val visitorData = visitorSnapshot.getValue(VisitorUserData::class.java)
//                        if (visitorData!!.entryDate == "11/02/2024") {
//                            Toast.makeText(this@HistoryActivity, "Yes", Toast.LENGTH_SHORT).show()
//                        }
//                        else{
//                            Toast.makeText(this@HistoryActivity, "No", Toast.LENGTH_SHORT).show()
//                        }
                        visitorUserList.add(visitorData!!)
                    }
                    try {
                        binding.historyDataRecyclerView.layoutManager = LinearLayoutManager(
                            this@HistoryActivity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                        binding.historyDataRecyclerView.setHasFixedSize(true)
                        adapter = VisitorDataAdapter(visitorUserList, this@HistoryActivity)
                        binding.historyDataRecyclerView.adapter = adapter
                        adapter.setOnItemClickListener(object :
                            VisitorDataAdapter.OnItemClickListener {
                            override fun onItemClick(visitorData: VisitorUserData) {
                                val intent =
                                    Intent(
                                        this@HistoryActivity,
                                        HistoryUserActivity::class.java
                                    )
                                intent.putExtra("image", visitorData.image)
                                intent.putExtra("name", visitorData.name)
                                intent.putExtra("mobile", visitorData.mobile)
                                intent.putExtra("buildingName", visitorData.buildingName)
                                intent.putExtra("flatNo", visitorData.flatNo)
                                intent.putExtra("person", visitorData.person)
                                intent.putExtra("vehicleNo", visitorData.vehicleNo)
                                intent.putExtra("entryDate", visitorData.entryDate)
                                intent.putExtra("inTime", visitorData.inTime)
                                intent.putExtra("outTime", visitorData.outTime)
                                intent.putExtra("category", visitorData.category)
                                intent.putExtra("remark", visitorData.remark)
                                startActivity(intent)
                            }
                        })
                    } catch (e: Exception) {
                        Log.d(
                            "Exception in Recyclerview parthiv",
                            "onDataChange: ${e.message}"
                        )
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@HistoryActivity,
                        "Database error: ${databaseError.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@HistoryActivity, MainActivity::class.java))
        finish()
    }
}