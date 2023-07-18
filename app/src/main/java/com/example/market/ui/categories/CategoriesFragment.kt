package com.example.market.ui.categories

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.market.R
import com.example.market.data.pojo.Product
import com.example.market.databinding.FragmentCategoriesBinding
import com.example.market.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton

@AndroidEntryPoint
class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoriesViewModel by viewModels()
    private val productsAdapter by lazy {
        ProductsAdapter(object : ProductsAdapter.ProductClickListener {
            override fun onItemClicked(product: Product) {
                //navigate to product info
            }
        })
    }

    private var mainCategory = ""
    private var subCategory = ""

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

        observeSearchButton()
        setupProductsRecyclerView()
        observeProductsResponse()
        observeButtonsGroup()
        observeFloatingActionButton()

        viewModel.getProducts()
    }

    private fun observeSearchButton() {
        binding.ivSearch.setOnClickListener {
            findNavController().navigate(CategoriesFragmentDirections.actionCategoriesFragmentToSearchFragment())
        }
    }

    private fun observeProductsResponse() {
        viewModel.products.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let {
                        productsAdapter.submitList(it)
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
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        }
    }

    private fun observeButtonsGroup() {
        binding.category.setOnSelectListener { button: ThemedButton ->
            when (button.id) {
                R.id.btn_women -> {
                    Log.d("setOnSelectListener", "btn_women")
                    mainCategory = WOMEN
                    viewModel.filterProducts(mainCategory, subCategory)
                }
                R.id.btn_kid -> {
                    Log.d("setOnSelectListener", "btn_kid")
                    mainCategory = KID
                    viewModel.filterProducts(mainCategory, subCategory)
                }
                R.id.btn_men -> {
                    Log.d("setOnSelectListener", "btn_men")
                    mainCategory = MEN
                    viewModel.filterProducts(mainCategory, subCategory)
                }
                R.id.btn_sale -> {
                    Log.d("setOnSelectListener", "btn_sale")
                    mainCategory = SALE
                    viewModel.filterProducts(mainCategory, subCategory)
                }
            }
        }
    }

    private fun observeFloatingActionButton() {
        binding.fabShoes.setOnClickListener {
            Log.d("setOnSelectListener", "fabShoes")
            subCategory = SHOES
            viewModel.filterProducts(mainCategory, subCategory)
        }

        binding.fabShirt.setOnClickListener {
            Log.d("setOnSelectListener", "fabShirt")
            subCategory = T_SHIRTS
            viewModel.filterProducts(mainCategory, subCategory)
        }

        binding.fabAccessories.setOnClickListener {
            Log.d("setOnSelectListener", "fabAccessories")
            subCategory = ACCESSORIES
            viewModel.filterProducts(mainCategory, subCategory)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val WOMEN = "WOMEN"
        const val KID = "KID"
        const val MEN = "MEN"
        const val SALE = "SALE"
        const val SHOES = "SHOES"
        const val T_SHIRTS = "T-SHIRTS"
        const val ACCESSORIES = "ACCESSORIES"
    }
}