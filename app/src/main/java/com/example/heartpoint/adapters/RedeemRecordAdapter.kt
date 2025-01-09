package com.example.heartpoint.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.heartpoint.R
import com.example.heartpoint.models.RedeemRecord
import java.text.SimpleDateFormat
import java.util.Locale

class RedeemRecordAdapter(private val redeemRecordList: List<RedeemRecord>) :
    RecyclerView.Adapter<RedeemRecordAdapter.RedeemRecordViewHolder>() {

    inner class RedeemRecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_gift_title)
        val tvDate: TextView = itemView.findViewById(R.id.tv_redeem_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RedeemRecordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_redeem_record, parent, false)
        return RedeemRecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RedeemRecordViewHolder, position: Int) {
        val record = redeemRecordList[position]

        // Bind data to the view
        holder.tvTitle.text = record.title

        // Format the date and set it
        holder.tvDate.text = "兌換日期：${formatDate(record.date)}"
    }

    override fun getItemCount(): Int {
        return redeemRecordList.size
    }

    // Date formatting function
    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(timestamp)
    }
}
