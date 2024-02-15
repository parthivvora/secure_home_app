package com.example.securehome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.securehome.databinding.ActivityHistoryUserBinding

@Suppress("DEPRECATION")
class HistoryUserActivity : AppCompatActivity() {
    lateinit var binding: ActivityHistoryUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
            finish()
        }

        val image = intent.extras!!.get("image").toString()
        val name = intent.extras!!.get("name").toString()
        val mobile = intent.extras!!.get("mobile").toString()
        val buildingName = intent.extras!!.get("buildingName").toString()
        val flatNo = intent.extras!!.get("flatNo").toString()
        val person = intent.extras!!.get("person").toString()
        val vehicleNo = intent.extras!!.get("vehicleNo").toString()
        val entryDate = intent.extras!!.get("entryDate").toString()
        val inTime = intent.extras!!.get("inTime").toString()
        val outTime= intent.extras!!.get("outTime").toString()
        val category = intent.extras!!.get("category").toString()
        val remark = intent.extras!!.get("remark").toString()

        Glide.with(this)
            .load(image)
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
            .into(binding.userImage)
        binding.name.text = name
        binding.contact.text = mobile
        binding.buildingName.text = buildingName
        binding.flatNo.text = flatNo
        binding.visitDate.text = entryDate
        binding.inTime.text = inTime
        binding.outTime.text = outTime
        binding.category.text = category
        binding.person.text = person
        binding.vehicleNo.text = vehicleNo
        binding.remarks.text = remark
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@HistoryUserActivity, HistoryActivity::class.java))
        finish()
    }
}