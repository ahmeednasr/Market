package com.example.market.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.market.R
import com.example.market.data.pojo.PriceRule
import com.smarteist.autoimageslider.SliderViewAdapter

class DiscountAdapter(
    private val context: Context,
    private val clickListener: DiscountClickListener
) : SliderViewAdapter<DiscountAdapter.Holder>() {

    private var prices: ArrayList<PriceRule> = ArrayList()

    fun renewItems(prices: ArrayList<PriceRule>) {
        this.prices = prices
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(viewHolder: Holder, position: Int) {
        if (prices[position].title.equals("Summer25")) {
            viewHolder.imageView.setImageDrawable(context.resources.getDrawable(R.drawable.twenty_five))
        } else if (prices[position].title.equals("FreeShipping")){
            viewHolder.imageView.setImageDrawable(context.resources.getDrawable(R.drawable.hundred))
        }
        viewHolder.textView.text = prices[position].title

        viewHolder.layout.setOnClickListener {
            clickListener.onItemClicked(prices[position])
        }
    }

    override fun getCount(): Int {
        return prices.size
    }

    interface DiscountClickListener {
        fun onItemClicked(discount: PriceRule)
    }

    inner class Holder(itemView: View) : ViewHolder(itemView) {
        var layout: View
        var imageView: ImageView
        var textView: TextView

        init {
            layout = itemView.findViewById(R.id.layout)
            imageView = itemView.findViewById(R.id.iv_discount)
            textView = itemView.findViewById(R.id.tv_discount_name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?): Holder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_ad, parent, false)
        return Holder(view)
    }
}