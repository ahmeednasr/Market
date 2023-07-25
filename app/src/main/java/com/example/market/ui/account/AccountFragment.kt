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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.market.R
import com.example.market.auth.AuthActivity
import com.example.market.data.pojo.Currency
import com.example.market.data.pojo.LineItemsItem
import com.example.market.databinding.FragmentAccountBinding
import com.example.market.databinding.LanguagePopupBinding
import com.example.market.utils.Constants
import com.example.market.utils.Constants.ARABIC
import com.example.market.utils.Constants.CURRENCY_FROM_KEY
import com.example.market.utils.Constants.CURRENCY_TO_KEY
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
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    lateinit var dialog: AlertDialog
    lateinit var currentLocale: Locale
    lateinit var currentLanguage: String

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private val viewModel: AccountViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    private val favouritesAccountAdapter by lazy {
        FavouritesAccountAdapter(object : FavouritesAccountAdapter.ClickListener {
            override fun onItemClicked(product: LineItemsItem) {
                product.sku?.toLong()?.let {
                    findNavController().navigate(
                        AccountFragmentDirections.actionAccountFragmentToProductDetails(
                            it
                        )
                    )
                }
            }
        })
    }

    private val ordersAccountAdapter by lazy { OrdersAccountAdapter() }

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
        editor = sharedPreferences.edit()

        updateUserUI()
        setupProductsRecyclerView()
        setupOrdersRecyclerView()
        observeOrdersResponse()
        observeProductsResponse()
        observeLoginButton()
        observeSearchButton()
        navigateToOrders()
        navigateToFavourites()

        viewModel.getOrders()
        viewModel.getFavourites()

        currentLocale = Locale.getDefault()
        currentLanguage = currentLocale.language
        if (currentLanguage == "en" || currentLanguage.isEmpty()) {
            binding.languageValue.text = resources.getString(R.string.english)
        } else if (currentLanguage == "ar") {
            binding.languageValue.text = resources.getString(R.string.arabic)
        }
        binding.currencyValue.text = sharedPreferences.getString(CURRENCY_TO_KEY, "") ?: "EGP"
        binding.llLanguage.setOnClickListener {
            showDialog()
        }

        binding.llCurrency.setOnClickListener {
            observeCurrenciesResponse()
        }
        binding.llAddress.setOnClickListener {
            findNavController().navigate(AccountFragmentDirections.actionAccountFragmentToAddressFormFragment())
        }
        binding.ivCart.setOnClickListener {

        }
        observeSearchButton()
        navigateToOrders()
    }

    private fun navigateToOrders() {
        binding.llOrders.setOnClickListener {
            if (sharedPreferences.getBoolean(Constants.IS_Logged, false)) {
                findNavController().navigate(AccountFragmentDirections.actionAccountFragmentToOrdersFragment())
            } else {
                showAlertDialog()
            }
        }
    }

    private fun navigateToFavourites() {
        binding.llOrders.setOnClickListener {
            if (sharedPreferences.getBoolean(Constants.IS_Logged, false)) {
                findNavController().navigate(AccountFragmentDirections.actionAccountFragmentToFavouritesFragment())
            } else {
                showAlertDialog()
            }
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(resources.getString(R.string.login_required))
        builder.setMessage(resources.getString(R.string.alert_msg))
        builder.setIcon(android.R.drawable.ic_dialog_info)
        builder.setPositiveButton(resources.getString(R.string.OK)) { _, _ ->
            val i = Intent(requireActivity(), AuthActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(i)
            activity?.finish()
        }
        builder.setNegativeButton(resources.getString(R.string.cancel)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
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
        editor.putString(CURRENCY_TO_KEY, selectedItem as String?)
        //val currencyFrom=
        editor.apply()
        binding.currencyValue.text = selectedItem
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
                Firebase.auth.signOut()
            } else {
                startActivity(Intent(requireActivity(), AuthActivity::class.java))
            }
            updateUserUI()
        }
    }

    private fun observeProductsResponse() {
        viewModel.products.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let {
                        Log.d("observeProductsResponse", "size: ${it.size}")
                        favouritesAccountAdapter.submitList(it.take(2))
                    }
                }
                is NetworkResult.Error -> {
                }
                is NetworkResult.Loading -> {
                }
            }
        }
    }

    private fun setupProductsRecyclerView() {
        binding.rvFavourites.apply {
            adapter = favouritesAccountAdapter
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        }
    }

    private fun observeOrdersResponse() {
        viewModel.orders.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.orders?.let {
                        Log.d("observeProductsResponse", "size: ${it.size}")
                        ordersAccountAdapter.submitList(it.take(2))
                    }
                }
                is NetworkResult.Error -> {

                }
                is NetworkResult.Loading -> {

                }
            }
        }
    }

    private fun setupOrdersRecyclerView() {
        binding.rvOrders.apply {
            adapter = ordersAccountAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
