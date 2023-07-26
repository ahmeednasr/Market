package com.example.market.ui.cart

import android.content.Context
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
import com.example.market.R
import com.example.market.data.pojo.LineItemsItem
import com.example.market.data.pojo.Product
import com.example.market.databinding.FragmentCartBinding
import com.example.market.utils.Constants
import com.example.market.utils.Constants.CURRENCY_FROM_KEY
import com.example.market.utils.Constants.SharedPreferences
import com.example.market.utils.Constants.UserID
import com.example.market.utils.NetworkResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CartFragment : Fragment(), CartClickListener {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter: CartAdapter
    val viewModel: CartViewModel by viewModels()
    private lateinit var currency: String

    @Inject
    lateinit var sharedPreferences: SharedPreferences
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
        viewModel.getCartItems()
        viewModel.convertCurrency()
        val to = sharedPreferences.getString(Constants.CURRENCY_TO_KEY, "") ?: "EGP"
        adapter = CartAdapter(requireContext(), this, to, 51.3)
        binding.CartRecuclerView.adapter = adapter
        viewModel.conversionResult.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let {
                        val sharedPreferences = requireContext().getSharedPreferences(
                            Constants.SharedPreferences,
                            Context.MODE_PRIVATE
                        )

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
        binding.ivBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.CartRecuclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewModel.cart.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let {
                        Log.i("LOLOLO", "$it")
                        adapter.setCartList(it)
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

    override fun addProduct(product: Product, variantId: Long) {

    }

    override fun deleteProduct(product: Product, variantId: Long) {
    }

    override fun removeCartItem(lineItemsItem: LineItemsItem) {
        viewModel.deleteCartItem(lineItemsItem)
    }
}