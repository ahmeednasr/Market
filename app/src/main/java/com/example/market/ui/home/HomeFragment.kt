package com.example.market.ui.home

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.market.R
import com.example.market.auth.AuthActivity
import com.example.market.data.pojo.PriceRule
import com.example.market.databinding.FragmentHomeBinding
import com.example.market.utils.Constants
import com.example.market.utils.Constants.DISCOUNT_ID
import com.example.market.utils.NetworkResult
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPreferences: SharedPreferences

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
    private val discountAdapter by lazy {
        DiscountAdapter( requireContext(),
            object : DiscountAdapter.DiscountClickListener {
            override fun onItemClicked(discount: PriceRule) {
                val alertDialog: AlertDialog? = activity?.let {
                    val builder = AlertDialog.Builder(it)
                    builder.apply {
                        setTitle("you get discount code")
                        setMessage(discount.title)
                        setPositiveButton(
                            R.string.save,
                            DialogInterface.OnClickListener { dialog, id ->
                                val sharedPreferences = requireContext().getSharedPreferences(
                                    Constants.SharedPreferences, 0
                                )
                                val editor = sharedPreferences.edit()
                                editor.putString(DISCOUNT_ID, discount.title)
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

        })
    }
    private val layoutManager by lazy {
        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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

        observeFavouritesButton()
        observeSearchButton()
        setupBrandsRecyclerView()
        observeBrandsResponse()
        observeDiscountResponse()
        observeCartButton()

        binding.rvDiscount.setSliderAdapter(discountAdapter)
        binding.rvDiscount.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.rvDiscount.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION)
        binding.rvDiscount.startAutoCycle()

        viewModel.getDiscountCodes()
        viewModel.getBrands()
    }

    private fun observeSearchButton() {
        binding.ivSearch.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchFragment())
        }
    }

    private fun observeFavouritesButton() {
        binding.ivFavourite.setOnClickListener {
            if (sharedPreferences.getBoolean(Constants.IS_Logged, false)) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFavouritesFragment())
            } else {
                showAlertDialog()
            }
        }
    }

    private fun observeCartButton() {
        binding.ivCart.setOnClickListener {
            if (sharedPreferences.getBoolean(Constants.IS_Logged, false)) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCartFragment())
            } else {
                showAlertDialog()
            }
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(resources.getString(R.string.login_required))
        builder.setMessage(resources.getString(R.string.alert_msg))
        builder.setIcon(android.R.drawable.ic_dialog_info)
        builder.setPositiveButton(resources.getString(R.string.OK)) { _, _ ->
            val i = Intent(requireActivity(), AuthActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(i)
            activity?.finish()
        }
        builder.setNegativeButton(resources.getString(R.string.cancel)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
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
                    response.data?.let {
                        discountAdapter.renewItems(it.price_rules as ArrayList<PriceRule>)
                    }
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