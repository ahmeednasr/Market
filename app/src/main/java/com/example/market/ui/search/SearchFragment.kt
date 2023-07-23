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

import com.example.market.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel:SearchViewModel by viewModels()
    var products = ArrayList<Product>()


    private val searchAdapter by lazy {
        SearchAdapter(object : SearchAdapter.ProductClickListener {
            override fun onItemClicked(product: Product) {
                findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToProductDetails(product))
            }

            override fun onFavouriteClicked(product: Product) {
                //add product to favourites
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
                filteredList(newText)
                return true
            }
        })

    }

    private fun filteredList(text: String?) {
        if (text != null) {
            if (text.isNotEmpty()) {
                val filteredList = ArrayList<Product>()
                for (product in products) {
                    if (product.title!!.toLowerCase().contains(text, false)) {
                        filteredList.add(product)
                    }
                }
                searchAdapter.submitList(filteredList)
            }else{
                searchAdapter.submitList(null)
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
                    response.data?.let {
                        Log.d("observeProductsResponse", "size: ${it.size}")
                        if (it.isEmpty()){
                            handleNoDataState()
                        } else {
                            products.addAll(it)
                            handleDataState()
                        }
                    }
                }
                is NetworkResult.Error -> {
                }
                is NetworkResult.Loading -> {
                }
            }
        }
    }

}