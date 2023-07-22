package com.example.market.ui.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.market.R
import com.example.market.data.pojo.Product
import com.example.market.databinding.FragmentSearchBinding
import kotlin.math.abs
import com.example.market.utils.NetworkResult
import com.google.android.material.slider.Slider
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel:SearchViewModel by viewModels()

    private val searchAdapter by lazy {
        SearchAdapter(object : SearchAdapter.ProductClickListener {
            override fun onItemClicked(product: Product) {
                findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToProductDetails(product))
            }

            override fun onFavouriteClicked(product: Product) {
                if (product.isFavourite) {
                    viewModel.deleteFavourite(product)
                } else {
                    viewModel.addFavourite(product)
                }
                product.isFavourite = !product.isFavourite
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

        viewModel.getProducts()
         setupSliderView()
        setupProductsRecyclerView()
        observeProductsResponse()


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

        binding.continuousSlider.addOnChangeListener(object: Slider.OnChangeListener {
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
            return@setLabelFormatter "$${value.roundToInt()}"
        }
    }

    private fun setupProductsRecyclerView() {
        binding.rvProducts.apply {
            adapter = searchAdapter
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        }
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
                        if (it.isEmpty()){
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