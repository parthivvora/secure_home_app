package com.example.securehome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class VisitorActivity : AppCompatActivity() {
    private lateinit var newRecyclerView: RecyclerView
    private lateinit var newArrayList: ArrayList<VisitorData>
    private lateinit var visitorImage: Array<Int>
    private lateinit var visitDate: Array<String>
    private lateinit var visitInTime: Array<String>
    private lateinit var visitOutTime: Array<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visitor)

        visitorImage = arrayOf(
            R.drawable.user,
            R.drawable.user,
            R.drawable.user,
            R.drawable.user,
            R.drawable.user,
            R.drawable.user,
            R.drawable.user,
            R.drawable.user
        )
        visitDate = arrayOf(
            "8 Aug 2023",
            "9 Aug 2023",
            "10 Aug 2023",
            "11 Aug 2023",
            "12 Aug 2023",
            "13 Aug 2023",
            "14 Aug 2023",
            "15 Aug 2023"
        )
        visitInTime = arrayOf(
            "7:46 PM",
            "8:46 PM",
            "9:46 PM",
            "10:46 PM",
            "7:46 PM",
            "8:46 PM",
            "9:46 PM",
            "10:46 PM"
        )
        visitOutTime = arrayOf(
            "12:46 PM",
            "1:46 PM",
            "2:46 PM",
            "3:46 PM",
            "12:46 PM",
            "1:46 PM",
            "2:46 PM",
            "3:46 PM"
        )

        newRecyclerView = findViewById(R.id.visitorDataRecyclerView)
//        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        newRecyclerView.setHasFixedSize(true)

        newArrayList = arrayListOf<VisitorData>()
        getVisitorData()
    }

    private fun getVisitorData() {
        for (i in visitorImage.indices) {
            val data = VisitorData(visitorImage[i], visitDate[i], visitInTime[i], visitOutTime[i])
            newArrayList.add(data)
        }
        val adapter = VisitorDataAdapter(newArrayList, this)
        newRecyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : VisitorDataAdapter.OnItemClickListener {
            override fun onItemClick(visitorData: VisitorData) {
                val intent = Intent(this@VisitorActivity, VisitorUserActivity::class.java)
                intent.putExtra("image", visitorData.image)
                intent.putExtra("date", visitorData.date)
                intent.putExtra("inTime", visitorData.inTime)
                intent.putExtra("outTime", visitorData.outTime)
                startActivity(intent)
            }
        })
    }
}