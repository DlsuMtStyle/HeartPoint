package com.example.heartpoint.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.heartpoint.R
import com.example.heartpoint.models.Store

class StoreAdapter(private val storeList: List<Store>) :
    RecyclerView.Adapter<StoreAdapter.StoreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.store_item, parent, false)
        return StoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val store = storeList[position]
        holder.title.text = store.title
        holder.category.text = store.category
        holder.date.text = store.date
        holder.loveCount.text = store.love_count

        // 使用 Glide 加載圖片
        Glide.with(holder.image.context)
            .load(store.image)
            .into(holder.image)
    }

    override fun getItemCount(): Int = storeList.size

    class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.img_store)
        val title: TextView = itemView.findViewById(R.id.tv_store_title)
        val category: TextView = itemView.findViewById(R.id.tv_store_category)
        val date: TextView = itemView.findViewById(R.id.tv_store_date)
        val loveCount: TextView = itemView.findViewById(R.id.tv_store_love_count)
    }
}
