package com.example.market.ui.cart

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.market.databinding.FragmentCartBinding
import com.example.market.utils.Constants
import com.example.market.utils.Constants.UserID
import com.example.market.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment(), CartClickListener {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter: CartAdapter
    val viewModel: CartViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences =
            requireContext().getSharedPreferences(Constants.SharedPreferences, 0)
        val id = sharedPreferences.getString(UserID, "")
        Log.i("LOLOLO", "idddddd= ${id?.toLong()}")
        if (id != null) {
            viewModel.getCartItems(id.toLong())
        }
        adapter = CartAdapter(requireContext())
        binding.CartRecuclerView.adapter = adapter
        binding.CartRecuclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewModel.cart.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let {
                        Log.i("LOLOLO", "${it.draft_orders}")
                        adapter.setCartList(it.draft_orders)
                    }
                }
                is NetworkResult.Error -> {
                    Log.i("LOLOLO", "is ${response.message}")
                }
                is NetworkResult.Loading -> {
                    Log.i("LOLOLO", "${response}")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun addProduct() {
        TODO("Not yet implemented")
    }

    override fun deleteProduct() {
        TODO("Not yet implemented")
    }

    override fun removeCartItem(cartId: Long) {
        TODO("Not yet implemented")
    }

}