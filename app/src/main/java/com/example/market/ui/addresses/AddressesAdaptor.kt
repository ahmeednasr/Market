package com.example.market.ui.addresses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.market.data.pojo.Address
import com.example.market.databinding.ItemAddressBinding

class AddressesAdaptor(
    private val clickListener : AddressClickListener
    ) :
    ListAdapter<Address,AddressesAdaptor.MyViewHolder>(
        DailyDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class MyViewHolder(private val binding: ItemAddressBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(address: Address, clickListener: AddressClickListener) {
            binding.apply {
                tvPhone.text = address.phone
                tvGovernment.text = address.province
                tvAddressStreet.text = address.address1
                if(rbAddress.isChecked){
                    clickListener.onSelectedClicked(address)
                }else{
                    clickListener.onItemDeSelected(address)
                }
                ivDelete.setOnClickListener {
                    clickListener.onItemDeleted(address)
                }
            }
        }


        companion object {
            fun from(parent: ViewGroup): AddressesAdaptor.MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemAddressBinding.inflate(layoutInflater, parent, false)
                return AddressesAdaptor.MyViewHolder(binding)
            }
        }
    }

    interface AddressClickListener {
        fun onSelectedClicked(address: Address)
        fun onItemDeSelected(address: Address)
        fun onItemDeleted(address: Address)
    }

    class DailyDiffCallback : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }


}