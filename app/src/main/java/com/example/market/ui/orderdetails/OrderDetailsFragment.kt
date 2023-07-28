package com.example.market.ui.orderdetails

import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.market.databinding.FragmentOrderDetailsBinding
import com.example.market.utils.Constants
import com.example.market.utils.NetworkManager
import com.example.market.utils.NetworkResult
import com.example.market.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrderDetailsFragment : Fragment() {

    private var _binding: FragmentOrderDetailsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private var exchangeRate: Double? = null

    private val viewModel: OrderDetailsViewModel by viewModels()
    private val orderDetailsAdapter by lazy {
        OrderDetailsAdapter(
            sharedPreferences.getString(Constants.CURRENCY_TO_KEY, "") ?: "EGP"
        )
    }

    private val args: OrderDetailsFragmentArgs by navArgs()

    @Inject
    lateinit var networkChangeListener: NetworkManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exchangeRate = sharedPreferences.getFloat(Constants.Exchange_Value, 0.0F).toDouble()

        registerNetworkManager()
        observeNetworkState()
        observeBackButton()
        setupOrdersRecyclerView()
        observeOrdersResponse()
    }

    private fun observeBackButton() {
        binding.ivBackArrow.setOnClickListener {
            findNavController().popBackStack()
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

    private fun observeNetworkState() {
        NetworkManager.isNetworkAvailable.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getOrder(args.orderId)
                handleWhenThereNetwork()
            } else {
                handleWhenNoNetwork()
            }
        }
    }

    private fun handleWhenOrdersResponseError() {
        binding.apply {
            rvProducts.visibility = View.GONE
        }
    }

    private fun handleWhenNoNetwork() {
        handleWhenOrdersResponseError()
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

    private fun observeOrdersResponse() {
        viewModel.orders.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    stopShimmer()
                    response.data?.order?.line_items?.let {
                        Log.d("observeProductsResponse", "size: ${it}")
                        if (it.isEmpty()) {
                            handleNoDataState()
                        } else {
                            handleDataState()
                            orderDetailsAdapter.exchangeRate = exchangeRate
                            orderDetailsAdapter.submitList(it.filter { product ->
                                !product.title.equals(Constants.TITTLE)
                            })
                        }
                    }
                }
                is NetworkResult.Error -> {
                    stopShimmer()
                    Utils.showErrorSnackbar(binding.root, "Error happened")
                }
                is NetworkResult.Loading -> {
                    startShimmer()
                }
            }
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

    private fun setupOrdersRecyclerView() {
        binding.rvProducts.apply {
            adapter = orderDetailsAdapter
            layoutManager = LinearLayoutManager(requireContext())
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}