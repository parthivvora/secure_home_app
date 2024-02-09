package com.example.securehome

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VisitorDataAdapter(
    private val visitorDataList: ArrayList<VisitorData>,
    private val context: Context
) :
    RecyclerView.Adapter<VisitorDataAdapter.VisitorDataViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(visitorData: VisitorData)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
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
        holder.visitorImage.setImageResource(currentItem.image)
        holder.visitDate.text = currentItem.date
        holder.visitInTime.text = currentItem.inTime
        holder.visitOutTime.text = currentItem.outTime

        holder.viewVisitorDataIcon.setOnClickListener {
            listener?.onItemClick(currentItem)
        }
    }

    class VisitorDataViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val visitorImage: ImageView = itemView.findViewById(R.id.visitorImage)
        val visitDate: TextView = itemView.findViewById(R.id.visitDate)
        val visitInTime: TextView = itemView.findViewById(R.id.visitInTime)
        val visitOutTime: TextView = itemView.findViewById(R.id.visitOutTime)
        val viewVisitorDataIcon: ImageView = itemView.findViewById(R.id.viewVisitorDataIcon)
    }
}