package com.example.market.ui.favourites

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.market.data.pojo.Product
import com.example.market.databinding.FragmentFavouritesBinding

class FavouritesFragment : Fragment() {

    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!

    private val favouritesAdapter by lazy {
        FavouritesAdapter(object : FavouritesAdapter.ProductClickListener {
            override fun onItemClicked(product: Product) {
                //navigate to product details
            }

            override fun onDislikeClicked(product: Product) {
                //remove item from favourites
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

        observeBackButton()
        setupProductsRecyclerView()
        observeProductsResponse()

    }

    private fun observeBackButton() {
        binding.ivBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeProductsResponse() {
//        viewModel.products.observe(viewLifecycleOwner) { response ->
//            when (response) {
//                is NetworkResult.Success -> {
//                    response.data?.let {
//                        brandProductsAdapter.submitList(it.products)
//                    }
//                }
//                is NetworkResult.Error -> {
//
//                }
//                is NetworkResult.Loading -> {
//
//                }
//            }
//        }
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

    private fun setupProductsRecyclerView() {
        binding.rvProducts.apply {
            adapter = favouritesAdapter
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}