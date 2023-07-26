package com.example.market.ui.favourites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.market.data.pojo.LineItemsItem
import com.example.market.data.pojo.Product
import com.example.market.databinding.ItemFavouriteProductBinding
import java.math.RoundingMode
import java.text.DecimalFormat

class FavouritesAdapter(
    private val currency: String,
    private val clickListener: ProductClickListener
) :
    ListAdapter<LineItemsItem, FavouritesAdapter.MyViewHolder>(
        DailyDiffCallback()
    ) {

    var exchangeRate: Double? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener, exchangeRate, currency)
    }

    interface ProductClickListener {
        fun onItemClicked(product: LineItemsItem)
        fun onDislikeClicked(product: LineItemsItem)
    }

    class MyViewHolder(private val binding: ItemFavouriteProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: LineItemsItem, clickListener: ProductClickListener, exchangeRate: Double?, currency: String) {
            binding.apply {
                tvProductName.text = product.title
                val price = product.price?.toDouble()?.times(exchangeRate ?: 1.0)
                tvProductPrice.text = "$currency ${roundOffDecimal(price?:0.0)}"

                Glide
                    .with(binding.root)
                    .load(product.properties?.get(0)?.value)
                    .into(ivProduct)

                ivUnfavourite.setOnClickListener {
                    clickListener.onDislikeClicked(product)
                }

                cvLayout.setOnClickListener {
                    clickListener.onItemClicked(product)
                }
            }
        }

        private fun roundOffDecimal(number: Double): Double {
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING
            return df.format(number).toDouble()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemFavouriteProductBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }

    class DailyDiffCallback : DiffUtil.ItemCallback<LineItemsItem>() {
        override fun areItemsTheSame(oldItem: LineItemsItem, newItem: LineItemsItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LineItemsItem, newItem: LineItemsItem): Boolean {
            return oldItem == newItem
        }
    }
}