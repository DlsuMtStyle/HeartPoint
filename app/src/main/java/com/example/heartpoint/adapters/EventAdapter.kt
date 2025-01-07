package com.example.heartpoint.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.heartpoint.R
import com.example.heartpoint.models.Event

class EventAdapter(private val events: List<Event>, private val onItemClick: (Event) -> Unit) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_title)
        val location: TextView = itemView.findViewById(R.id.tv_location)
        val date: TextView = itemView.findViewById(R.id.tv_date)
        val image: ImageView = itemView.findViewById(R.id.img_event)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(events[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.title.text = event.title
        holder.location.text = event.location
        holder.date.text = event.date

        Glide.with(holder.itemView.context)
            .load(event.image)
            .placeholder(ColorDrawable(Color.LTGRAY))
            .into(holder.image)

        holder.itemView.setOnClickListener {
            onItemClick(event) // 點擊時傳遞 `event`
        }
    }

    override fun getItemCount() = events.size
}

