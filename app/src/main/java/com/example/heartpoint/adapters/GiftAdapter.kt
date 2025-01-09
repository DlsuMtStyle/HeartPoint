package com.example.heartpoint.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.heartpoint.R
import com.example.heartpoint.models.Gift
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GiftAdapter(private val giftList: MutableList<Gift>) :
    RecyclerView.Adapter<GiftAdapter.GiftViewHolder>() {

    inner class GiftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgGift: ImageView = itemView.findViewById(R.id.img_gift)
        val tvGiftTitle: TextView = itemView.findViewById(R.id.tv_gift_title)
        val btnUseGift: Button = itemView.findViewById(R.id.btnUseGift)

        // 绑定数据到 ViewHolder
        fun bind(gift: Gift) {
            tvGiftTitle.text = gift.title
            Glide.with(itemView.context)
                .load(gift.image)
                .into(imgGift)

            // 设置点击事件
            btnUseGift.setOnClickListener {
                Log.d("GiftAdapter", "使用按鈕被點擊")
                useGift(gift)
            }
        }

        // 使用礼物逻辑
        private fun useGift(gift: Gift) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val db = FirebaseFirestore.getInstance()
                val giftRef = db.collection("users")
                    .document(userId)
                    .collection("redeemed_gifts")
                    .document(gift.id)

                // 删除文件
                giftRef.delete()
                    .addOnSuccessListener {
                        Toast.makeText(itemView.context, "已使用該禮券！", Toast.LENGTH_SHORT).show()

                        // 从列表移除礼物
                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            giftList.removeAt(position) // 从列表中删除
                            notifyItemRemoved(position) // 通知 RecyclerView 数据已改变
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(
                            itemView.context,
                            "使用失敗：${exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                Toast.makeText(itemView.context, "無法確認用戶資訊", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 创建 ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiftViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_gift, parent, false)
        return GiftViewHolder(view)
    }

    // 绑定 ViewHolder
    override fun onBindViewHolder(holder: GiftViewHolder, position: Int) {
        val gift = giftList[position]
        holder.bind(gift) // 调用 bind 方法
    }

    // 返回项目数量
    override fun getItemCount(): Int {
        return giftList.size
    }
}
