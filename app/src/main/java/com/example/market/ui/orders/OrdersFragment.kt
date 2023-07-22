package com.example.market.ui.orders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.market.databinding.FragmentOrdersBinding
import com.example.market.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrdersViewModel by viewModels()
    private val ordersAdapter by lazy { OrdersAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeBackButton()
        setupProductsRecyclerView()
        observeOrdersResponse()

        viewModel.getOrders()
    }

    private fun observeBackButton() {
        binding.ivBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeOrdersResponse() {
        viewModel.orders.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let {

                    }
                }
                is NetworkResult.Error -> {

                }
                is NetworkResult.Loading -> {
                    startShimmer()
                }
            }
        }
    }

    private fun startShimmer() {
        binding.apply {
            rvOrders.visibility = View.GONE
            shimmerViewContainer.visibility = View.VISIBLE
            shimmerViewContainer.startShimmerAnimation()
        }
    }

    private fun stopShimmer() {
        binding.apply {
            rvOrders.visibility = View.VISIBLE
            shimmerViewContainer.visibility = View.GONE
            shimmerViewContainer.stopShimmerAnimation()
        }
    }

    private fun setupProductsRecyclerView() {
        binding.rvOrders.apply {
            adapter = ordersAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun handleNoDataState() {
        binding.apply {
            ivNoData.visibility = View.VISIBLE
            tvNoData.visibility = View.VISIBLE
            rvOrders.visibility = View.GONE
        }
    }

    private fun handleDataState() {
        binding.apply {
            ivNoData.visibility = View.GONE
            tvNoData.visibility = View.GONE
            rvOrders.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}