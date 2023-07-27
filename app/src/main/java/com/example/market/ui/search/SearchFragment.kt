package com.example.market.ui.search

import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.market.R
import com.example.market.auth.AuthActivity
import com.example.market.data.pojo.LineItemsItem
import com.example.market.data.pojo.Product
import com.example.market.databinding.FragmentSearchBinding
import com.example.market.utils.Constants
import com.example.market.utils.NetworkManager
import com.example.market.utils.NetworkResult
import com.google.android.material.slider.Slider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var networkChangeListener: NetworkManager

    private lateinit var currency: String
    private var exchangeRate: Double? = null

    private val searchAdapter by lazy {
        SearchAdapter(
            sharedPreferences.getString(Constants.CURRENCY_TO_KEY, "") ?: "EGP",
            object : SearchAdapter.ProductClickListener {
                override fun onItemClicked(product: Product) {
                    product.id?.let {
                        findNavController().navigate(
                            SearchFragmentDirections.actionSearchFragmentToProductDetails(
                                it
                            )
                        )
                    }
                }

                override fun onFavouriteClicked(product: Product) {
                    if (sharedPreferences.getBoolean(Constants.IS_Logged, false)) {
                        if (product.isFavourite) {
                            showDeleteAlertDialog(product)
                        } else {
                            viewModel.addFavourite(product)
                        }
                        product.isFavourite = !product.isFavourite
                    } else {
                        showAlertDialog()
                    }
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currency = sharedPreferences.getString(Constants.CURRENCY_TO_KEY, "") ?: "EGP"

        registerNetworkManager()
        observeNetworkState()
        observeBackButton()
        setupSliderView()
        setupProductsRecyclerView()
        observeProductsResponse()
        observeSearchText()
        observeSliderChange()
        observeConversionResult()
    }

    private fun observeConversionResult() {
        viewModel.conversionResult.observe(viewLifecycleOwner){
            searchAdapter.exchangeRate = it
            exchangeRate = it
            initSliderValues()
        }
    }

    private fun initSliderValues() {
        binding.apply {
            exchangeRate?.let {
                tvMax.text = "${(300*it).roundToInt()} ${currency}"
                tvMin.text = "${(0*it).roundToInt()} ${currency}"
                tvSlider.text = "${(0*it).roundToInt()} ${currency}"
            }
        }
    }

    private fun observeBackButton() {
        binding.ivBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeSearchText() {
        binding.etSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    viewModel.filterProductsByTittle(newText)
                } else {
                    searchAdapter.submitList(viewModel.allProducts)
                }
                return true
            }
        })
    }

    private fun observeSliderChange() {
        binding.continuousSlider.addOnChangeListener(object : Slider.OnChangeListener {
            override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
                Log.d("addOnChangeListener", slider.value.toString())
                if (value < 5.0f) {
                    searchAdapter.submitList(viewModel.allProducts)
                } else {
                    viewModel.filterProductsByPrice(value)
                }
            }
        })
    }

    private fun registerNetworkManager() {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        ContextCompat.registerReceiver(
            requireActivity(),
            networkChangeListener,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onPause() {
        super.onPause()
        activity?.let {
            LocalBroadcastManager.getInstance(it).unregisterReceiver(networkChangeListener)
        }
    }

    private fun observeNetworkState() {
        NetworkManager.isNetworkAvailable.observe(viewLifecycleOwner) {
            if (it){
                viewModel.convertCurrency(
                    "EGP",
                    currency,
                    1.00
                )
                viewModel.getProducts()
                handleWhenThereNetwork()
            } else {
                handleWhenNoNetwork()
            }
        }
    }

    private fun handleWhenProductsResponseError() {
        binding.apply {
            rvProducts.visibility = View.GONE
            etSearch.visibility = View.GONE
            continuousSlider.visibility = View.GONE
            tvMax.visibility = View.GONE
            tvMin.visibility = View.GONE
            tvSlider.visibility = View.GONE
            tvFilter.visibility = View.GONE
        }
    }

    private fun handleWhenNoNetwork() {
        handleWhenProductsResponseError()
        binding.apply {
            ivNoConnection.visibility = View.VISIBLE
            tvNoConnection.visibility = View.VISIBLE
        }
    }

    private fun handleWhenThereNetwork() {
        binding.apply {
            continuousSlider.visibility = View.VISIBLE
            tvMax.visibility = View.VISIBLE
            tvMin.visibility = View.VISIBLE
            tvSlider.visibility = View.VISIBLE
            tvFilter.visibility = View.VISIBLE
            rvProducts.visibility = View.VISIBLE
            rvProducts.visibility = View.VISIBLE
            ivNoConnection.visibility = View.GONE
            tvNoConnection.visibility = View.GONE
        }
    }

    private fun handleNoDataState() {
        binding.apply {
            ivNoData.visibility = View.VISIBLE
            tvNoData.visibility = View.VISIBLE
            rvProducts.visibility = View.GONE
        }
    }

    private fun handleDataState() {
        binding.apply {
            ivNoData.visibility = View.GONE
            tvNoData.visibility = View.GONE
            rvProducts.visibility = View.VISIBLE
        }
    }

    private fun startShimmer() {
        binding.apply {
            rvProducts.visibility = View.GONE
            shimmerViewContainer.visibility = View.VISIBLE
            shimmerViewContainer.startShimmerAnimation()
        }
    }

    private fun stopShimmer() {
        binding.apply {
            rvProducts.visibility = View.VISIBLE
            shimmerViewContainer.visibility = View.GONE
            shimmerViewContainer.stopShimmerAnimation()
        }
    }

    private fun setupSliderView() {
        binding.continuousSlider.setLabelFormatter { value: Float ->
            //should change $ to current currency
            binding.tvSlider.text = "${(value *(exchangeRate ?: 1.0)).roundToInt()} ${currency}"
            return@setLabelFormatter "${(value * (exchangeRate ?: 1.0)).roundToInt()} ${currency}"
        }
    }

    private fun setupProductsRecyclerView() {
        binding.rvProducts.apply {
            itemAnimator = null
            adapter = searchAdapter
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        }
    }

    private fun showDeleteAlertDialog(product: Product) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(resources.getString(R.string.wraning))
        builder.setMessage(resources.getString(R.string.wraning_remove))
        builder.setIcon(android.R.drawable.ic_dialog_info)
        builder.setPositiveButton(resources.getString(R.string.OK)) { _, _ ->
            viewModel.deleteFavourite(product)
        }
        builder.setNegativeButton(resources.getString(R.string.cancel)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeProductsResponse() {
        viewModel.products.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    stopShimmer()
                    response.data?.let {
                        Log.d("observeProductsResponse", "size: ${it.size}")
                        if (it.isEmpty()) {
                            handleNoDataState()
                        } else {
                            handleDataState()
                            searchAdapter.submitList(it)
                        }
                    }
                }
                is NetworkResult.Error -> {
                    stopShimmer()
                }
                is NetworkResult.Loading -> {
                    startShimmer()
                }
            }
        }
    }

}