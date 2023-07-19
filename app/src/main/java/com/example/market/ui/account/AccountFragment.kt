package com.example.market.ui.account

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.market.R
import com.example.market.data.pojo.Currency
import com.example.market.databinding.FragmentAccountBinding
import com.example.market.ui.brand.BrandViewModel
import com.example.market.utils.Constants.CURRENCY_KEY
import com.example.market.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
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
        //viewModel.getCurrencies()
        observeSearchButton()

        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_authIntro)
        }

        binding.llCurrency.setOnClickListener {
            observeProductsResponse()
        }
    }

    private fun observeSearchButton() {
        binding.ivSearch.setOnClickListener {
            findNavController().navigate(AccountFragmentDirections.actionAccountFragmentToSearchFragment())
        }
    }

    private fun observeProductsResponse() {
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
        val sharedPreferences = requireContext().getSharedPreferences("PREFS", 0)
        val editor = sharedPreferences.edit()
        editor.putString(CURRENCY_KEY, selectedItem as String?)
        editor.apply()
        val key = sharedPreferences.getString(CURRENCY_KEY, "")

        Toast.makeText(requireContext(), key, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}