package com.example.heartpoint.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.heartpoint.R
import com.example.heartpoint.models.EventRecord
import java.text.SimpleDateFormat
import java.util.Locale

class EventRecordAdapter(private val eventRecordList: List<EventRecord>) :
    RecyclerView.Adapter<EventRecordAdapter.EventRecordViewHolder>() {

    inner class EventRecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_event_title)
        val tvDate: TextView = itemView.findViewById(R.id.tv_apply_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventRecordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event_record, parent, false)
        return EventRecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventRecordViewHolder, position: Int) {
        val record = eventRecordList[position]

        // Bind data to the view
        holder.tvTitle.text = record.title

        // Format the date and set it
        holder.tvDate.text = "報名日期：${formatDate(record.date)}"
    }

    override fun getItemCount(): Int {
        return eventRecordList.size
    }

    // Date formatting function
    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(timestamp)
    }
}
