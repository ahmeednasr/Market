package com.example.market.ui.home

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.market.R
import com.example.market.data.pojo.PriceRule
import com.example.market.databinding.ItemDiscountBinding

class DiscountAdapter(
    private val clickListener: DiscountClickListener
) :
    ListAdapter<PriceRule, DiscountAdapter.MyViewHolder>(
        DailyDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    interface DiscountClickListener {
        fun onItemClicked(discount: PriceRule)
    }

    class MyViewHolder(private val binding: ItemDiscountBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(discount: PriceRule, clickListener: DiscountClickListener) {
            binding.apply {
                tvDiscountName.text = discount.title
                Glide
                    .with(binding.root)
                    .load(R.drawable.copon)
                    .into(ivDiscount)
                cvDiscountLayout.setOnClickListener {
                    clickListener.onItemClicked(discount)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDiscountBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    class DailyDiffCallback : DiffUtil.ItemCallback<PriceRule>() {
        override fun areItemsTheSame(oldItem: PriceRule, newItem: PriceRule): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: PriceRule,
            newItem: PriceRule
        ): Boolean {
            return oldItem == newItem
        }
    }
}