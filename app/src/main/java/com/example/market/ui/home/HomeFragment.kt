package com.example.market.ui.home

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.market.R
import com.example.market.databinding.FragmentHomeBinding
import com.example.market.utils.Constants
import com.example.market.utils.Constants.DISCOUNT_ID
import com.example.market.utils.NetworkResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private val brandsAdapter by lazy {
        BrandsAdapter(object : BrandsAdapter.BrandClickListener {
            override fun onItemClicked(vendor: String) {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToBrandFragment(
                        vendor
                    )
                )
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSearchButton()
        setupBrandsRecyclerView()
        observeBrandsResponse()
        observeDiscountResponse()
        binding.discountBtn.setOnClickListener {
            viewModel.getDiscountCodes()
        }
        binding.ivCart.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCartFragment())
        }
        viewModel.getBrands()
    }

    private fun observeSearchButton() {
        binding.ivSearch.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchFragment())
        }
    }

    private fun observeBrandsResponse() {
        viewModel.brands.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    stopShimmer()
                    response.data?.let {
                        brandsAdapter.submitList(it.smart_collections)
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

    private fun startShimmer() {
        binding.apply {
            cvBrands.visibility = View.GONE
            shimmerViewContainer.visibility = View.VISIBLE
            shimmerViewContainer.startShimmerAnimation()
        }
    }

    private fun stopShimmer() {
        binding.apply {
            cvBrands.visibility = View.VISIBLE
            shimmerViewContainer.visibility = View.GONE
            shimmerViewContainer.stopShimmerAnimation()
        }
    }

    private fun setupBrandsRecyclerView() {
        binding.rvBrands.apply {
            adapter = brandsAdapter
            set3DItem(true)
            setAlpha(true)
            setInfinite(true)
            setOrientation(RecyclerView.HORIZONTAL)
        }
    }

    private fun observeDiscountResponse() {
        viewModel.discountCodes.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    val discountCode = response.data?.discount_codes?.get(0)?.code
                    val alertDialog: AlertDialog? = activity?.let {
                        val builder = AlertDialog.Builder(it)
                        builder.apply {
                            setTitle("congrats. you get discount code")
                            setMessage(discountCode)
                            setPositiveButton(
                                R.string.save,
                                DialogInterface.OnClickListener { dialog, id ->
                                    val sharedPreferences = requireContext().getSharedPreferences(
                                        Constants.SharedPreferences, 0
                                    )
                                    val editor = sharedPreferences.edit()
                                    editor.putString(DISCOUNT_ID, discountCode)
                                    editor.apply()
                                })
                            setNegativeButton(R.string.cancel,
                                DialogInterface.OnClickListener { dialog, id ->
                                })
                        }
                        builder.create()
                    }
                    alertDialog?.show()
                }
                is NetworkResult.Error -> {

                }
                is NetworkResult.Loading -> {

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}