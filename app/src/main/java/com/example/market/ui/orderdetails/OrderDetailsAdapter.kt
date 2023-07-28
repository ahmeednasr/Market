package com.example.market.ui.orderdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.market.data.pojo.LineItem
import com.example.market.data.pojo.Order
import com.example.market.databinding.ItemOrderBinding
import com.example.market.databinding.ItemOrderDetailBinding
import com.example.market.utils.Utils

class OrderDetailsAdapter(
    private val currency: String
) : ListAdapter<LineItem, OrderDetailsAdapter.MyViewHolder>(
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

    class MyViewHolder(private val binding: ItemOrderDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: LineItem, exchangeRate: Double?, currency: String) {
            binding.apply {
                tvTitle.text = product.title

                val price = product.price?.toDouble()?.times(exchangeRate ?: 1.0)
                tvPrice.text = "Price: ${Utils.roundOffDecimal(price ?: 0.0)} $currency"

                tvQuantity.text = "Quantity: ${product.quantity}"

                Glide
                    .with(binding.root)
                    .load(product.properties?.get(0)?.value)
                    .into(ivProduct)
            }
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemOrderDetailBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }

    class DailyDiffCallback : DiffUtil.ItemCallback<LineItem>() {
        override fun areItemsTheSame(oldItem: LineItem, newItem: LineItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LineItem, newItem: LineItem): Boolean {
            return oldItem == newItem
        }
    }
}