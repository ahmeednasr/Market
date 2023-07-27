package com.example.market.ui.favourites

import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.market.R
import com.example.market.data.pojo.LineItemsItem
import com.example.market.databinding.FragmentFavouritesBinding
import com.example.market.utils.Constants
import com.example.market.utils.NetworkManager
import com.example.market.utils.NetworkResult
import com.example.market.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavouritesFragment : Fragment() {

    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavouritesViewModel by viewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var networkChangeListener: NetworkManager

    private lateinit var currency: String

    private val favouritesAdapter by lazy {
        FavouritesAdapter(sharedPreferences.getString(Constants.CURRENCY_TO_KEY, "") ?: "EGP",
            object : FavouritesAdapter.ProductClickListener {
                override fun onItemClicked(product: LineItemsItem) {
                    product.sku?.toLong()?.let {
                        findNavController().navigate(
                            FavouritesFragmentDirections.actionFavouritesFragmentToProductDetails(
                                it
                            )
                        )
                    }
                }
                override fun onDislikeClicked(product: LineItemsItem) {
                    showDeleteAlertDialog(product)
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currency = sharedPreferences.getString(Constants.CURRENCY_TO_KEY, "") ?: "EGP"
        registerNetworkManager()
        observeNetworkState()
        observeBackButton()
        setupProductsRecyclerView()
        observeProductsResponse()

        viewModel.conversionResult.observe(viewLifecycleOwner) {
            favouritesAdapter.exchangeRate = it
        }
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

    private fun observeBackButton() {
        binding.ivBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeNetworkState() {
        NetworkManager.isNetworkAvailable.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getFavourites()
                viewModel.convertCurrency("EGP", currency, 1.00)
                handleWhenThereNetwork()
            } else {
                handleWhenNoNetwork()
            }
        }
    }

    private fun handleWhenFavouritesResponseError() {
        binding.apply {
            rvProducts.visibility = View.GONE
        }
    }

    private fun handleWhenNoNetwork() {
        handleWhenFavouritesResponseError()
        binding.apply {
            ivNoConnection.visibility = View.VISIBLE
            tvNoConnection.visibility = View.VISIBLE
        }
    }

    private fun handleWhenThereNetwork() {
        binding.apply {
            rvProducts.visibility = View.VISIBLE
            ivNoConnection.visibility = View.GONE
            tvNoConnection.visibility = View.GONE
        }
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
                            favouritesAdapter.submitList(it)
                        }
                    }
                }
                is NetworkResult.Error -> {
                    stopShimmer()
                    handleWhenFavouritesResponseError()
                    Utils.showErrorSnackbar(binding.root, "Error happened")
                }
                is NetworkResult.Loading -> {
                    startShimmer()
                }
            }
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

    private fun setupProductsRecyclerView() {
        binding.rvProducts.apply {
            adapter = favouritesAdapter
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        }
    }

    private fun showDeleteAlertDialog(product: LineItemsItem) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}