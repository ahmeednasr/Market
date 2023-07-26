package com.example.market.ui.cart

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.market.R
import com.example.market.data.pojo.CartDraftOrder
import com.example.market.data.pojo.Currency
import com.example.market.data.pojo.DraftOrderResponse
import com.example.market.data.pojo.LineItemsItem
import com.example.market.databinding.ItemCartBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CartAdapter(
    val ctx: Context,
    private val onClick: CartClickListener,
    val currentCurrency: String, val convertedAmount: Double,
) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    lateinit var binding: ItemCartBinding
    private var cartList = mutableListOf<LineItemsItem>()

    class ViewHolder(var binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemCartBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = cartList[position]

        binding.productName.text = current.name
        binding.currentPrice.text = (current.price!!.toDouble() * convertedAmount).toString()
        binding.currency.text = currentCurrency
        Glide.with(ctx).load(current.properties?.get(0)?.value)
            .apply(RequestOptions().override(300, 250))
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background).into(holder.binding.productIcon)
        binding.itemCount.text = current.quantity.toString()
        binding.removeBtn.setOnClickListener {
            MaterialAlertDialogBuilder(ctx)
                .setTitle(ctx.resources.getString(R.string.app_name))
                .setMessage(ctx.resources.getString(R.string.rm_msg))
                .setNeutralButton(ctx.resources.getString(R.string.cancel)) { dialog, which ->
                    // Respond to neutral button press
                }
                .setNegativeButton(ctx.resources.getString(R.string.delete)) { dialog, which ->
                    // Respond to positive button press
                    if (current.id != null) {
                        onClick.removeCartItem(current)
                    }
                }
                .show()
        }
        binding.addItem.setOnClickListener {
            //onClick.addProduct(current,)
        }


    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    fun setCartList(list: List<LineItemsItem>) {
        cartList = list as MutableList<LineItemsItem>
        notifyDataSetChanged()
    }
}