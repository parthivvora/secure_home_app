package com.example.securehome.dataAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.securehome.R
import com.example.securehome.dataModel.VisitorUserData

class VisitorDataAdapter(
    private var visitorDataList: ArrayList<VisitorUserData>,
    private val context: Context
) :
    RecyclerView.Adapter<VisitorDataAdapter.VisitorDataViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(visitorData: VisitorUserData)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(visitorDataList: ArrayList<VisitorUserData>) {
        this.visitorDataList = visitorDataList
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitorDataViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.visitor_data_list_view, parent, false)
        return VisitorDataViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return visitorDataList.size
    }

    override fun onBindViewHolder(holder: VisitorDataViewHolder, position: Int) {
        val currentItem = visitorDataList[position]
        Glide.with(context).load(currentItem.image).into(holder.visitorImage)
        holder.visitDate.text = currentItem.entryDate
        holder.visitInTime.text = currentItem.inTime
        holder.visitOutTime.text = currentItem.outTime

        holder.viewVisitorDataIcon.setOnClickListener {
            listener?.onItemClick(currentItem)
        }
    }

    class VisitorDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val visitDate: TextView
        val visitInTime: TextView
        val visitOutTime: TextView
        val visitorImage: ImageView
        val viewVisitorDataIcon: ImageView

        init {
            visitDate = itemView.findViewById(R.id.visitDate)
            visitInTime = itemView.findViewById(R.id.visitInTime)
            visitOutTime = itemView.findViewById(R.id.visitOutTime)
            visitorImage = itemView.findViewById(R.id.visitorImage)
            viewVisitorDataIcon = itemView.findViewById(R.id.viewVisitorDataIcon)
        }
    }
}