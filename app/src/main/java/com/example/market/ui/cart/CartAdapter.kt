package com.example.market.ui.cart

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.market.R
import com.example.market.data.pojo.LineItemsItem
import com.example.market.databinding.ItemCartBinding
import com.example.market.utils.Utils.roundOffDecimal
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CartAdapter(
    private val currency: String,
    private val onClick: CartClickListener,
    private val ctx: Context
) :
    ListAdapter<LineItemsItem, CartAdapter.MyViewHolder>(
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
        holder.bind(getItem(position), onClick, exchangeRate, currency, ctx)
    }

    class MyViewHolder(var binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            current: LineItemsItem,
            onClick: CartClickListener,
            exchangeRate: Double?,
            currency: String,
            ctx: Context
        ) {
            binding.productName.text = current.name
            val price = current.price?.toDouble()?.times(exchangeRate ?: 1.0)
            binding.currentPrice.text = "${roundOffDecimal(price ?: 0.0)}"
            binding.currency.text = currency
            Glide.with(ctx).load(current.properties?.get(0)?.value)
                .apply(RequestOptions().override(300, 250))
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background).into(binding.productIcon)
            binding.itemCount.text = current.quantity.toString()
            val max = current.properties?.get(1)?.value
            binding.stockNumber.text = max
            binding.removeBtn.setOnClickListener {
                MaterialAlertDialogBuilder(ctx)
                    .setTitle(ctx.resources.getString(R.string.app_name))
                    .setMessage(ctx.resources.getString(R.string.rm_msg))
                    .setNeutralButton(ctx.resources.getString(R.string.cancel)) { dialog, which ->
                    }
                    .setNegativeButton(ctx.resources.getString(R.string.delete)) { dialog, which ->
                        if (current.id != null) {
                            onClick.removeCartItem(current)
                        }
                    }
                    .show()
            }
            binding.addItem.setOnClickListener {
                if (isAddActive) {
                    var currentQuantity = current.quantity
                    if (currentQuantity!! < max!!.toInt()) {
                        currentQuantity += 1
                        binding.itemCount.text = currentQuantity.toString()
                        val price = current.price?.toDouble()?.times(exchangeRate ?: 1.0)

                        onClick.addProduct(
                            current,
                            max.toInt(),
                            currentQuantity,
                            roundOffDecimal(price ?: 0.0)
                        )

                    } else {
                        Toast.makeText(ctx, "cant add more", Toast.LENGTH_SHORT).show()
                    }
                }
                binding.addItem.isEnabled = false
                addHandler.postDelayed({
                    binding.addItem.isEnabled = true
                }, 650)

            }
            binding.rmItem.setOnClickListener {
                if (isDecrementActive) {
                    var currentQuantity = current.quantity
                    if (currentQuantity!! > 1) {
                        currentQuantity -= 1
                        binding.itemCount.text = currentQuantity.toString()
                        val price = current.price?.toDouble()?.times(exchangeRate ?: 1.0)
                        onClick.deleteProduct(current, roundOffDecimal(price ?: 0.0))
                    } else {
                        MaterialAlertDialogBuilder(ctx)
                            .setTitle(ctx.resources.getString(R.string.app_name))
                            .setMessage(ctx.resources.getString(R.string.rm_msg))
                            .setNeutralButton(ctx.resources.getString(R.string.cancel)) { dialog, which ->
                            }
                            .setNegativeButton(ctx.resources.getString(R.string.delete)) { dialog, which ->
                                if (current.id != null) {
                                    onClick.removeCartItem(current)
                                }
                            }
                            .show()
                    }
                }
                binding.rmItem.isEnabled = false
                decremHandler.postDelayed({
                    binding.rmItem.isEnabled = true
                }, 650)
            }
        }

        companion object {
            var isAddActive = true
            var isDecrementActive = true
            val addHandler = android.os.Handler()
            val decremHandler = android.os.Handler()
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCartBinding.inflate(layoutInflater, parent, false)
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