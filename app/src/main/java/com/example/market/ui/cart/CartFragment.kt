package com.example.market.ui.cart

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.market.R
import com.example.market.data.pojo.LineItemsItem
import com.example.market.data.pojo.Product
import com.example.market.databinding.FragmentCartBinding
import com.example.market.ui.home.HomeFragmentDirections
import com.example.market.utils.Constants
import com.example.market.utils.Constants.CURRENCY_FROM_KEY
import com.example.market.utils.Constants.SharedPreferences
import com.example.market.utils.Constants.UserID
import com.example.market.utils.NetworkResult
import com.example.market.utils.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CartFragment : Fragment(), CartClickListener {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    val viewModel: CartViewModel by viewModels()
    private lateinit var currency: String

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private val cartAdapter by lazy {
        CartAdapter(
            sharedPreferences.getString(Constants.CURRENCY_TO_KEY, "") ?: "EGP",
            this,
            requireContext()
        )
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
        viewModel.getCartItems()
        currency = sharedPreferences.getString(Constants.CURRENCY_TO_KEY, "") ?: "EGP"
        setupCartRecyclerView()
        observeCartResponse()

        viewModel.conversionResult.observe(viewLifecycleOwner) {
            cartAdapter.exchangeRate = it
        }
        binding.totalCurrancy.text = currency
        viewModel.convertCurrency("EGP", currency, 1.00)
        viewModel.subtotal.observe(viewLifecycleOwner) { subTotal ->
            binding.subTotalPrice.text = String.format("%.1f", subTotal.toDouble())
        }
        binding.ivBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.checkoutBtn.setOnClickListener {
            findNavController().navigate(CartFragmentDirections.actionCartFragmentToPaymentFragment())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun observeCartResponse() {
        viewModel.cart.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let {
                        if (it.isEmpty()) {
                            binding.emptyCart.visibility = View.VISIBLE
                            binding.emptyCardTxt.visibility = View.VISIBLE
                            binding.cartTitle.visibility = View.INVISIBLE
                            binding.subtotalTv.visibility = View.INVISIBLE
                            binding.totalCurrancy.visibility = View.INVISIBLE
                            binding.subTotalPrice.visibility = View.INVISIBLE

                            cartAdapter.submitList(it)
                        } else {
                            binding.emptyCart.visibility = View.GONE
                            binding.emptyCardTxt.visibility = View.GONE
                            cartAdapter.submitList(it)
                        }

                    }
                }
                is NetworkResult.Error -> {
                }
                is NetworkResult.Loading -> {
                }
            }
        }
    }

    private fun setupCartRecyclerView() {
        binding.CartRecuclerView.apply {
            adapter = cartAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    override fun addProduct(lineItemsItem: LineItemsItem, max: Int, current: Int) {
        if (current < max) {
            viewModel.addNewQuantityToCart(lineItemsItem)
        }
    }

    override fun deleteProduct(lineItemsItem: LineItemsItem) {
        viewModel.removeQuantityFromCart(lineItemsItem)
    }


    override fun removeCartItem(lineItemsItem: LineItemsItem) {
        viewModel.deleteCartItem(lineItemsItem)
    }
}