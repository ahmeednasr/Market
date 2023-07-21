package com.example.market.ui.brand

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.market.data.pojo.Product
import com.example.market.databinding.FragmentBrandBinding
import com.example.market.ui.categories.ProductsAdapter
import com.example.market.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BrandFragment : Fragment() {

    private var _binding: FragmentBrandBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BrandViewModel by viewModels()
    private val brandProductsAdapter by lazy {
        BrandProductsAdapter(object : BrandProductsAdapter.ProductClickListener {
            override fun onItemClicked(product: Product) {
                //navigate to product info
                findNavController().navigate(BrandFragmentDirections.actionBrandFragmentToProductDetails(product))
            }
        })
    }

    private val args: BrandFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBrandBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle()
        observeBackButton()
        setupProductsRecyclerView()
        observeProductsResponse()

        viewModel.getProducts(args.vendor)
    }

    private fun setTitle() {
        binding.tvTitle.text = args.vendor
    }

    private fun observeBackButton() {
        binding.ivBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeProductsResponse() {
        viewModel.products.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let {
                        brandProductsAdapter.submitList(it.products)
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
            adapter = brandProductsAdapter
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}