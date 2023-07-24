package com.example.market.ui.account

import android.content.Context
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.market.R
import com.example.market.auth.AuthActivity
import com.example.market.data.pojo.Currency
import com.example.market.databinding.FragmentAccountBinding
import com.example.market.databinding.LanguagePopupBinding
import com.example.market.utils.Constants
import com.example.market.utils.Constants.ARABIC
import com.example.market.utils.Constants.CURRENCY_KEY
import com.example.market.utils.Constants.CURRENCY_VALUE
import com.example.market.utils.Constants.ENGLISH
import com.example.market.utils.Constants.LANGUAGE_KEY
import com.example.market.utils.NetworkResult
import com.example.market.utils.Utils.setLocale
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    lateinit var dialog: AlertDialog
    lateinit var currentLocale: Locale
    lateinit var currentLanguage: String

    private val viewModel: AccountViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

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

        auth = Firebase.auth
        updateUserUI()
        sharedPreferences = requireContext().getSharedPreferences(Constants.SharedPreferences, 0)
        editor = sharedPreferences.edit()

        updateUserUI()
        observeLoginButton()
        observeSearchButton()
        observeConvertCurrencyResponse()
        navigateToOrders()

        currentLocale = Locale.getDefault()
        currentLanguage = currentLocale.language
        if (currentLanguage == "en" || currentLanguage.isEmpty()) {
            binding.languageValue.text = resources.getString(R.string.english)
        } else if (currentLanguage == "ar") {
            binding.languageValue.text = resources.getString(R.string.arabic)
        }
        binding.currencyValue.text = sharedPreferences.getString(CURRENCY_KEY, "") ?: "EGP"
        binding.llLanguage.setOnClickListener {
            showDialog()
        }

        binding.llCurrency.setOnClickListener {
            observeCurrenciesResponse()
        }
        binding.llAddress.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_addressFormFragment)
        }
        binding.ivCart.setOnClickListener {

        }
        observeSearchButton()
        observeConvertCurrencyResponse()
        navigateToOrders()
    }

    private fun navigateToOrders() {
        binding.llOrders.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_ordersFragment)
        }
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
        // val oldCurrency = sharedPreferences.getString(CURRENCY_KEY, "")
        editor.putString(CURRENCY_KEY, selectedItem as String?)
        editor.apply()
        binding.currencyValue.text = selectedItem
        viewModel.convertCurrency("EGP", selectedItem!!, 1.0)
        Toast.makeText(requireContext(), selectedItem, Toast.LENGTH_SHORT).show()

    }

    private fun showDialog() {

        var popUpBinding = LanguagePopupBinding.inflate(layoutInflater)
        currentLocale = Locale.getDefault()
        currentLanguage = currentLocale.language
        Log.i("LANGUAGE", currentLanguage)
        if (currentLanguage == "en" || currentLanguage.isEmpty()) {
            popUpBinding.english.isChecked = true
        } else if (currentLanguage == "ar") {
            popUpBinding.arabic.isChecked = true
        }
        popUpBinding.languageGroup.setOnCheckedChangeListener { _, checkedId ->

            when (checkedId) {
                R.id.english -> {
                    setLocale(ENGLISH, requireContext())
                    editor.putString(LANGUAGE_KEY, ENGLISH)
                }
                R.id.arabic -> {
                    setLocale(ARABIC, requireContext())
                    editor.putString(LANGUAGE_KEY, ARABIC)
                }
            }
            editor.apply()
            requireActivity().recreate()
        }

        dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(popUpBinding.root).create()
        dialog.show()
    }

    private fun updateUserUI() {
        if (auth.currentUser != null) {
            binding.tvLogin.text = "Logout"
            binding.tvUsername.text = auth.currentUser!!.email
        } else {
            binding.tvLogin.text = "Login"
            binding.tvUsername.text = ""
        }
    }

    private fun observeLoginButton() {
        binding.tvLogin.setOnClickListener {
            if (auth.currentUser != null) {
                editor.putBoolean(Constants.IS_Logged, false)
                editor.apply()
                Toast.makeText(requireContext(), "Logged Out", Toast.LENGTH_SHORT).show()
                Firebase.auth.signOut()
            } else {
                startActivity(Intent(requireActivity(), AuthActivity::class.java))
            }
            updateUserUI()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
