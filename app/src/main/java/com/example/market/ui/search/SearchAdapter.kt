package com.example.market.ui.search

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.market.R
import com.example.market.data.pojo.Product
import com.example.market.databinding.ItemFavouriteProductBinding
import com.example.market.databinding.ItemSearchProductBinding
import com.example.market.utils.Utils.roundOffDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

class SearchAdapter(
    private val currency: String,
    private val clickListener: ProductClickListener
) :
    ListAdapter<Product, SearchAdapter.MyViewHolder>(
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
        holder.bind(getItem(position), clickListener, exchangeRate, currency)

        if (getItem(position).isFavourite) {
            Log.d("bind", "bind " + getItem(position).title!!)
            holder.binding.ivFavourite.setImageDrawable(holder.binding.root.context.getDrawable(R.drawable.ic_filled_heart))
        } else {
            holder.binding.ivFavourite.setImageDrawable(holder.binding.root.context.getDrawable(R.drawable.ic_heart))
        }

        holder.binding.ivFavourite.setOnClickListener {
            clickListener.onFavouriteClicked(getItem(position))
            notifyItemChanged(position)
        }
    }

    interface ProductClickListener {
        fun onItemClicked(product: Product)
        fun onFavouriteClicked(product: Product)
    }

    class MyViewHolder(val binding: ItemSearchProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            product: Product,
            clickListener: ProductClickListener,
            exchangeRate: Double?,
            currency: String
        ) {
            binding.apply {
                tvProductName.text = product.title
                val price = product.variants?.get(0)?.price?.toDouble()?.times(exchangeRate ?: 1.0)
                tvProductPrice.text = "${roundOffDecimal(price ?: 0.0)} $currency"

                Glide
                    .with(binding.root)
                    .load(product.image?.src)
                    .into(ivProduct)

                cvLayout.setOnClickListener {
                    clickListener.onItemClicked(product)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSearchProductBinding.inflate(layoutInflater, parent, false)
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