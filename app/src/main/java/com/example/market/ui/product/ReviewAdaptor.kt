package com.example.market.ui.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.market.databinding.ReviewesItemBinding

data class UserReview(val userName:String,val userReview:String)

class ReviewAdaptor : ListAdapter<UserReview,ReviewAdaptor.MyViewHolder>(
    DailyDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return ReviewAdaptor.MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ReviewAdaptor.MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MyViewHolder(private val binding: ReviewesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(userReview: UserReview) {
            binding.apply {
                binding.userName.text =  userReview.userName
                binding.userReview.text = userReview.userReview
            }
        }
        companion object {
            fun from(parent: ViewGroup): ReviewAdaptor.MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ReviewesItemBinding.inflate(layoutInflater, parent, false)
                return ReviewAdaptor.MyViewHolder(binding)
            }
        }
    }

    class DailyDiffCallback : DiffUtil.ItemCallback<UserReview>() {
        override fun areItemsTheSame(oldItem: UserReview, newItem: UserReview): Boolean {
            return oldItem.userName == newItem.userName
        }

        override fun areContentsTheSame(oldItem: UserReview, newItem: UserReview): Boolean {
            return oldItem == newItem
        }
    }

}