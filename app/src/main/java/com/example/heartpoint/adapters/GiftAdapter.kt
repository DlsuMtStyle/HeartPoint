package com.example.heartpoint.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.heartpoint.R
import com.example.heartpoint.models.Gift

class GiftAdapter(private var giftList: List<Gift>) :
    RecyclerView.Adapter<GiftAdapter.GiftViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiftViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.gift_item, parent, false) // 確保使用正確的 XML 布局
        return GiftViewHolder(view)
    }

    override fun onBindViewHolder(holder: GiftViewHolder, position: Int) {
        val gift = giftList[position]
        holder.title.text = gift.title
        holder.category.text = gift.category
        holder.date.text = gift.date

        // 顯示愛心數量
        holder.loveCount.text = gift.love_count.toString()

        // 使用 Glide 加載禮物圖片
        Glide.with(holder.image.context)
            .load(gift.image)
            .into(holder.image)

        // 使用 Glide 加載愛心圖片
        Glide.with(holder.loveImage.context)
            .load(gift.love_image)
            .into(holder.loveImage)
    }

    override fun getItemCount(): Int = giftList.size

    // 動態更新資料
    fun updateData(newGiftList: List<Gift>) {
        giftList = newGiftList
        notifyDataSetChanged()
    }

    class GiftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.img_gift) // 確保 XML 中 ID 正確
        val title: TextView = itemView.findViewById(R.id.tv_gift_title)
        val category: TextView = itemView.findViewById(R.id.tv_gift_category)
        val date: TextView = itemView.findViewById(R.id.tv_gift_date)
        val loveCount: TextView = itemView.findViewById(R.id.tv_gift_love_count)
        val loveImage: ImageView = itemView.findViewById(R.id.img_gift_love_icon) // 確保 XML 中 ID 正確
    }
}
