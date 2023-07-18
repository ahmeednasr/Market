package com.example.market.ui.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.market.databinding.FragmentCategoriesBinding
import com.example.market.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoriesViewModel by viewModels()
    private val productsAdapter by lazy { ProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupProductsRecyclerView()
        observeProductsResponse()

        viewModel.getProducts()
    }

    private fun observeProductsResponse() {
        viewModel.products.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let {
                        productsAdapter.submitList(it.products)
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
        binding.rvProducts.apply {
            adapter = productsAdapter
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}