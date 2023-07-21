package com.example.market.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.market.data.pojo.Product
import com.example.market.databinding.ItemFavouriteProductBinding

class SearchAdapter(
    private val clickListener: ProductClickListener
) :
    ListAdapter<Product, SearchAdapter.MyViewHolder>(
        DailyDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    interface ProductClickListener {
        fun onItemClicked(product: Product)
        fun onFavouriteClicked(product: Product)
    }

    class MyViewHolder(private val binding: ItemFavouriteProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product, clickListener: ProductClickListener) {
            binding.apply {
                tvProductName.text = product.title
                Glide
                    .with(binding.root)
                    .load(product.image?.src)
                    .into(ivProduct)

                ivFavourite.setOnClickListener {
                    clickListener.onFavouriteClicked(product)
                }

                cvLayout.setOnClickListener {
                    clickListener.onItemClicked(product)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemFavouriteProductBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }

    class DailyDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}