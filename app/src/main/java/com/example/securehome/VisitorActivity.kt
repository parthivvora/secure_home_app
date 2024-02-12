package com.example.securehome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.securehome.dataModel.UserDataModel
import com.example.securehome.dataModel.VisitorUserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class VisitorActivity : AppCompatActivity() {
    private lateinit var visitorDataRecyclerView: RecyclerView
    private lateinit var visitorUserList: ArrayList<VisitorUserData>
    private lateinit var adapter: VisitorDataAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private lateinit var backBtn: ImageView
    private lateinit var noDataTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var loadingTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visitor)

        backBtn = findViewById(R.id.backBtn)
        noDataTextView = findViewById(R.id.noDataTextView)
        progressBar = findViewById(R.id.progressBar)
        loadingTextView = findViewById(R.id.loadingTextView)

        backBtn.setOnClickListener {
            startActivity(Intent(this@VisitorActivity, MainActivity::class.java))
            finish()
        }

        visitorUserList = arrayListOf()
        visitorDataRecyclerView = findViewById(R.id.visitorDataRecyclerView)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            startActivity(Intent(this@VisitorActivity, LoginActivity::class.java))
            finish()
        } else {
            Log.d("visitor activity user id", "onStart: " + auth.currentUser!!.uid)
            userId = auth.currentUser!!.uid
            getVisitorData(userId)
        }
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
                    this@VisitorActivity,
                    "Database error: ${databaseError.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun fetchVisitors(userNumber: String) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Date()
        val todayDate = dateFormat.format(currentDate)
        progressBar.visibility = View.VISIBLE
        loadingTextView.visibility = View.VISIBLE

        val visitorsRef = FirebaseDatabase.getInstance().getReference("visitor")
        visitorsRef.orderByChild("mobile").equalTo(userNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    progressBar.visibility = View.GONE
                    loadingTextView.visibility = View.GONE
                    for (visitorSnapshot in dataSnapshot.children) {
                        val visitorData = visitorSnapshot.getValue(VisitorUserData::class.java)
                        if (visitorData!!.entryDate == todayDate) {
                            visitorUserList.add(visitorData)
                            try {
                                visitorDataRecyclerView.layoutManager = LinearLayoutManager(
                                    this@VisitorActivity,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                visitorDataRecyclerView.setHasFixedSize(true)
                                adapter = VisitorDataAdapter(visitorUserList, this@VisitorActivity)
                                visitorDataRecyclerView.adapter = adapter
                                adapter.setOnItemClickListener(object :
                                    VisitorDataAdapter.OnItemClickListener {
                                    override fun onItemClick(visitorData: VisitorUserData) {
                                        val intent =
                                            Intent(
                                                this@VisitorActivity,
                                                VisitorUserActivity::class.java
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
                        } else {
                            noDataTextView.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    progressBar.visibility = View.GONE
                    loadingTextView.visibility = View.GONE
                    Toast.makeText(
                        this@VisitorActivity,
                        "Database error: ${databaseError.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@VisitorActivity, MainActivity::class.java))
        finish()
    }
}