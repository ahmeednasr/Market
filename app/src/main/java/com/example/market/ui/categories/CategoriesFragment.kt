package com.example.market.ui.categories

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.market.R
import com.example.market.auth.AuthActivity
import com.example.market.data.pojo.Product
import com.example.market.databinding.FragmentCategoriesBinding
import com.example.market.ui.home.HomeFragmentDirections
import com.example.market.utils.Constants
import com.example.market.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton
import javax.inject.Inject

@AndroidEntryPoint
class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val viewModel: CategoriesViewModel by viewModels()
    private val productsAdapter by lazy {
        ProductsAdapter(object : ProductsAdapter.ProductClickListener {
            override fun onItemClicked(product: Product) {
                findNavController().navigate(
                    CategoriesFragmentDirections.actionCategoriesFragmentToProductDetails(
                        product.id!!
                    )
                )
            }
        })
    }

    private var mainCategory = ""
    private var subCategory = ""

    private var isAllFabsVisible: Boolean = false
    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var fabClock: Animation
    private lateinit var fabAntiClock: Animation

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

        binding.ivCart.setOnClickListener {

        }
        setFabAnimation()
        hideAllFabs()
        observeCategoryFab()
        observeFavouritesButton()
        observeSearchButton()
        setupProductsRecyclerView()
        observeProductsResponse()
        observeButtonsGroup()
        observeFloatingActionButton()

        viewModel.getProducts()
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

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Login Required")
        builder.setMessage("Please log in to continue.")
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

    private fun observeSearchButton() {
        binding.ivSearch.setOnClickListener {
            findNavController().navigate(CategoriesFragmentDirections.actionCategoriesFragmentToSearchFragment())
        }
    }

    private fun setFabAnimation() {
        fabClose = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_close)
        fabOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_open)
        fabClock = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_rotate_clock)
        fabAntiClock = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_rotate_anticlock)
    }

    private fun hideAllFabs() {
        binding.apply {
            fabAccessories.visibility = View.GONE
            fabAccessories.startAnimation(fabClose)
            fabShirt.visibility = View.GONE
            fabShirt.startAnimation(fabClose)
            fabShoes.visibility = View.GONE
            fabShoes.startAnimation(fabClose)
            fabCategory.startAnimation(fabAntiClock)
        }
    }

    private fun showAllFabs() {
        binding.apply {
            fabAccessories.visibility = View.VISIBLE
            fabAccessories.startAnimation(fabOpen)
            fabShirt.visibility = View.VISIBLE
            fabShirt.startAnimation(fabOpen)
            fabShoes.visibility = View.VISIBLE
            fabShoes.startAnimation(fabOpen)
            fabCategory.startAnimation(fabClock)
        }
    }

    private fun observeCategoryFab() {
        binding.fabCategory.setOnClickListener(View.OnClickListener {
            (if (!isAllFabsVisible) {
                showAllFabs()
            } else {
                hideAllFabs()
            }).also { isAllFabsVisible = !isAllFabsVisible }
        })
    }

    private fun observeProductsResponse() {
        viewModel.products.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    stopShimmer()
                    response.data?.let {
                        Log.d("observeProductsResponse", "size: ${it.size}")
                        if (it.isEmpty()) {
                            handleNoDataState()
                        } else {
                            productsAdapter.submitList(it)
                            handleDataState()
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