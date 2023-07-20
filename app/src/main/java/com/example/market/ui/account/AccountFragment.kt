package com.example.market.ui.account

import android.content.SharedPreferences
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.market.R
import com.example.market.auth.AuthActivity
import com.example.market.data.pojo.Currency
import com.example.market.data.pojo.OrderCurrency
import com.example.market.databinding.FragmentAccountBinding
import com.example.market.utils.Constants.CURRENCY_KEY
import com.example.market.utils.Constants.CURRENCY_VALUE
import com.example.market.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val viewModel: AccountViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("PREFS", 0)
        editor = sharedPreferences.edit()
        //viewModel.getCurrencies()
        observeSearchButton()
        binding.tvLogin.setOnClickListener {
            startActivity(Intent(requireActivity(),AuthActivity::class.java))
        }

        binding.llCurrency.setOnClickListener {
            observeCurrenciesResponse()
        }
        binding.llAddress.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_mapFragment)
        }
        observeConvertCurrencyResponse()
    }

    private fun observeSearchButton() {
        binding.ivSearch.setOnClickListener {
            findNavController().navigate(AccountFragmentDirections.actionAccountFragmentToSearchFragment())
        }
    }

    private fun observeCurrenciesResponse() {
        viewModel.currencies.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let {
                        Log.i("TAG", "${it.currencies}")
                        showCurrenciesMenu(binding.llCurrency, it.currencies)
                    }
                }
                is NetworkResult.Error -> {
                }
                is NetworkResult.Loading -> {

                }
            }
        }
    }

    private fun observeConvertCurrencyResponse() {
        viewModel.conversionResult.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let {
                        Log.i("TAG", "$it")
                        editor.putFloat(CURRENCY_VALUE, it.toFloat())
                        editor.apply()
                        val currencyValue = sharedPreferences.getFloat(CURRENCY_VALUE, 0.0F)
                        Toast.makeText(
                            requireContext(),
                            currencyValue.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                is NetworkResult.Error -> {
                }
                is NetworkResult.Loading -> {

                }
            }
        }
    }

    private fun showCurrenciesMenu(view: View, items: List<Currency>) {
        val popupMenu = PopupMenu(requireContext(), view)

        for (item in items) {
            popupMenu.menu.add(item.currency)
        }
        popupMenu.menu.add("EGP")

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            handleMenuItemClick(menuItem)
            true
        }

        popupMenu.show()
    }

    private fun handleMenuItemClick(menuItem: MenuItem) {
        val selectedItem = menuItem.title

        val oldCurrency = sharedPreferences.getString(CURRENCY_KEY, "")
        editor.putString(CURRENCY_KEY, selectedItem as String?)
        editor.apply()
        if (oldCurrency != null) {
            viewModel.convertCurrency(oldCurrency, selectedItem!!)
            Toast.makeText(requireContext(), selectedItem, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "old currency not found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}