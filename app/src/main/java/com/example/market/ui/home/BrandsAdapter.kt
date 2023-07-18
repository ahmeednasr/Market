package com.example.market.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.market.data.pojo.SmartCollection
import com.example.market.databinding.ItemBrandBinding

class BrandsAdapter :
    ListAdapter<SmartCollection, BrandsAdapter.MyViewHolder>(
        DailyDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MyViewHolder(private val binding: ItemBrandBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(brand: SmartCollection) {
            binding.apply {
                tvBrandName.text = brand.title
                Glide
                    .with(binding.root)
                    .load(brand.image?.src)
                    .into(ivBrand)
            }
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBrandBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    class DailyDiffCallback : DiffUtil.ItemCallback<SmartCollection>() {
        override fun areItemsTheSame(oldItem: SmartCollection, newItem: SmartCollection): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: SmartCollection,
            newItem: SmartCollection
        ): Boolean {
            return oldItem == newItem
        }
    }
}