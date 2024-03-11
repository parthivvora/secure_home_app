@file:Suppress("DEPRECATION")

package com.example.securehome

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

@Suppress("DEPRECATION", "NAME_SHADOWING")
class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var visitorUserList: ArrayList<VisitorUserData>
    private lateinit var adapter: VisitorDataAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private lateinit var progressDialog: ProgressDialog
    private var selectedMonth: String = ""
    private var currentMonth: Int = 0
    private var currentYear: Int = 0

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@HistoryActivity, R.layout.activity_history)

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this@HistoryActivity, MainActivity::class.java))
            finish()
        }

        visitorUserList = arrayListOf()
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            startActivity(Intent(this@HistoryActivity, LoginActivity::class.java))
            finish()
        } else {
            Log.d("HistoryActivity user id", "onStart: " + auth.currentUser!!.uid)
            userId = auth.currentUser!!.uid
            getVisitorData(userId)
        }

        // Get current year
        currentYear = LocalDate.now().year

        // Get Current, Previous and Next Month
        val calendar = Calendar.getInstance()
        currentMonth = calendar.get(Calendar.MONTH) + 1

        val monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US)
        binding.currentMonth.text = monthName

        binding.prevMonthBtn.setOnClickListener {
            if (currentMonth > 1) {
                currentMonth--
                calendar.set(Calendar.MONTH, currentMonth - 1)
                selectedMonth = String.format("%02d", currentMonth)
                val monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US)
                binding.currentMonth.text = monthName
                getVisitorData(userId)
                visitorUserList.clear()
                binding.historyDataRecyclerView.adapter?.notifyDataSetChanged()
            }
            if (currentMonth == 1) {
                binding.prevMonthBtn.isEnabled = false
            }
        }

        binding.nextMonthBtn.setOnClickListener {
            if (currentMonth >= 12) {
                binding.nextMonthBtn.isEnabled = false
            } else {
                currentMonth++
                calendar.set(Calendar.MONTH, currentMonth - 1)
                selectedMonth = String.format("%02d", currentMonth)
                val monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US)
                binding.currentMonth.text = monthName
                getVisitorData(userId)
                visitorUserList.clear()
                binding.historyDataRecyclerView.adapter?.notifyDataSetChanged()
                binding.nextMonthBtn.isEnabled = true
            }
        }

        // Progress dialog
        progressDialog = ProgressDialog(this@HistoryActivity)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    // Fetch login user buildingNo and flatNo
    private fun getVisitorData(userId: String) {
        val usersRef = FirebaseDatabase.getInstance().getReference("user").child(userId)
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(UserDataModel::class.java)
                user?.let {
                    val flatNo = user.flatNo
                    val buildingNo = user.buildingNo
                    flatNo?.let {
                        fetchVisitors(flatNo, buildingNo)
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

    // Fetch visitor information of login user using buildingNo and flatNo
    private fun fetchVisitors(flatNo: String, buildingNo: String?) {
        val visitorsRef = FirebaseDatabase.getInstance().getReference("visitor")
        visitorsRef.orderByChild("flatNo").equalTo(flatNo)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    progressDialog.dismiss()
                    for (visitorSnapshot in dataSnapshot.children) {
                        val visitorData = visitorSnapshot.getValue(VisitorUserData::class.java)
                        if (visitorData!!.flatNo == flatNo && visitorData.buildingName == buildingNo) {

                            val date = "${
                                visitorData.entryDate?.split("/")?.get(1)
                            }/${visitorData.entryDate?.split("/")?.get(2)}"

                            if (currentMonth < 10) {
                                if (date == "0$currentMonth/$currentYear") {
                                    visitorUserList.add(visitorData)
                                    visitorUserList.sortByDescending { it.entryDate }
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
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@HistoryActivity,
                        "Database error: $databaseError",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@HistoryActivity, MainActivity::class.java))
        finish()
    }
}