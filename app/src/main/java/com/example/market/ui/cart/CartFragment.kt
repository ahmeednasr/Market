package com.example.market.ui.cart

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.market.data.pojo.LineItemsItem
import com.example.market.databinding.FragmentCartBinding
import com.example.market.utils.Constants
import com.example.market.utils.Constants.Exchange_Value
import com.example.market.utils.NetworkResult
import com.example.market.utils.Utils.roundOffDecimal
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CartFragment : Fragment(), CartClickListener {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    val viewModel: CartViewModel by viewModels()
    private lateinit var currency: String
    var cartPrice: Double = 0.0


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
        // viewModel.convertCurrency("EGP", currency, 1.00)
        setupCartRecyclerView()
        observeCartResponse()
        Log.d("NEWNEW", sharedPreferences.getFloat(Exchange_Value, 1.0f).toString())

        cartAdapter.exchangeRate = (sharedPreferences.getFloat(Exchange_Value, 1.0f).toDouble())

        viewModel.subtotal.observe(viewLifecycleOwner) { sub ->
            Log.d("NEWNEW", sharedPreferences.getFloat(Exchange_Value, 1.0f).toString())
            cartPrice = sub * (sharedPreferences.getFloat(Exchange_Value, 1.0f).toDouble())
            Log.d("NEWNEW", "cartPrice:$cartPrice")
            binding.subTotalPrice.text = roundOffDecimal(cartPrice).toString()
        }
        binding.totalCurrancy.text = currency
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
                            hideView()
                            cartAdapter.submitList(it)
                        } else {
                            showView()
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

    override fun addProduct(
        lineItemsItem: LineItemsItem,
        max: Int,
        current: Int,
        currentPrice: Double
    ) {
        if (current <= max) {
            cartPrice += currentPrice
            Log.d("NEWNEW", "cartPrice:$cartPrice")
            binding.subTotalPrice.text =
                roundOffDecimal(cartPrice).toString()
            viewModel.addNewQuantityToCart(lineItemsItem)
        }
    }

    override fun deleteProduct(lineItemsItem: LineItemsItem, currentPrice: Double) {
        cartPrice -= currentPrice
        Log.d("NEWNEW", "cartPrice:$cartPrice")

        binding.subTotalPrice.text = roundOffDecimal(cartPrice).toString()
        viewModel.removeQuantityFromCart(lineItemsItem)
    }

    override fun removeCartItem(lineItemsItem: LineItemsItem) {
        viewModel.deleteCartItem(lineItemsItem)
    }

    private fun hideView() {
        binding.emptyCart.visibility = View.VISIBLE
        binding.emptyCardTxt.visibility = View.VISIBLE
        binding.cartTitle.visibility = View.INVISIBLE
        binding.subtotalTv.visibility = View.INVISIBLE
        binding.totalCurrancy.visibility = View.INVISIBLE
        binding.subTotalPrice.visibility = View.INVISIBLE
        binding.checkoutBtn.visibility = View.INVISIBLE
    }

    private fun showView() {
        binding.emptyCart.visibility = View.GONE
        binding.emptyCardTxt.visibility = View.GONE
    }
}