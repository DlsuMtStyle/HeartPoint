import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.heartpoint.R
import com.example.heartpoint.models.product

class StoreAdapter(
    private val productList: MutableList<product>,
    private val onItemClick: (product: product) -> Unit
) : RecyclerView.Adapter<StoreAdapter.StoreViewHolder>() {

    inner class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgStore: ImageView = itemView.findViewById(R.id.img_store)
        private val tvStoreTitle: TextView = itemView.findViewById(R.id.tv_store_title)
        private val tvStoreCategory: TextView = itemView.findViewById(R.id.tv_store_category)
        private val tvStoreDate: TextView = itemView.findViewById(R.id.tv_store_date)
        private val tvStoreLoveCount: TextView = itemView.findViewById(R.id.tv_store_love_count)

        fun bind(product: product) {
            // 設置商品資料
            tvStoreTitle.text = product.title
            tvStoreCategory.text = product.category
            tvStoreDate.text = "${product.startdate} ~ ${product.enddate}"
            tvStoreLoveCount.text = product.point.toString()

            // 使用 Glide 加載圖片
            Glide.with(itemView.context)
                .load(product.image)
                .into(imgStore)

            // 點擊事件
            itemView.setOnClickListener {
                onItemClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.store_item, parent, false)
        return StoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun updateData(newData: List<product>) {
        productList.clear()
        productList.addAll(newData)
        notifyDataSetChanged()
    }
}
