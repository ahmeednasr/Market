package com.example.market.ui.account

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.market.data.pojo.Order
import com.example.market.databinding.ItemOrderBinding
import com.example.market.utils.Utils

class OrdersAccountAdapter(
    private val currency: String
) : ListAdapter<Order, OrdersAccountAdapter.MyViewHolder>(
    DailyDiffCallback()
) {

    var exchangeRate: Double? = null
        set(value) {
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position), exchangeRate, currency)
    }

    class MyViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order, exchangeRate: Double?, currency: String) {
            binding.apply {
                tvOrderNumber.text = order.order_number.toString()
                val price = order.current_total_price?.toDouble()?.times(exchangeRate ?: 1.0)
                tvOrderPrice.text = "${Utils.roundOffDecimal(price ?: 0.0)} $currency"

                tvProductsNumber.text = (order.line_items?.size?.minus(1)).toString()
                tvDate.text = Utils.formatDate(order.created_at.toString())
            }
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemOrderBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }

    class DailyDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}