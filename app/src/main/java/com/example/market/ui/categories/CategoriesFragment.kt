package com.example.market.ui.categories

import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.market.R
import com.example.market.auth.AuthActivity
import com.example.market.data.pojo.Product
import com.example.market.databinding.FragmentCategoriesBinding
import com.example.market.ui.home.HomeFragmentDirections
import com.example.market.utils.Constants
import com.example.market.utils.NetworkManager
import com.example.market.utils.NetworkResult
import com.example.market.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton
import javax.inject.Inject

@AndroidEntryPoint
class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var networkChangeListener: NetworkManager

    private val viewModel: CategoriesViewModel by viewModels()
    private val productsAdapter by lazy {
        ProductsAdapter(object : ProductsAdapter.ProductClickListener {
            override fun onItemClicked(product: Product) {
                product.id?.let {
                    findNavController().navigate(
                        CategoriesFragmentDirections.actionCategoriesFragmentToProductDetails(
                            it
                        )
                    )
                }
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

        registerNetworkManager()
        observeNetworkState()
        setFabAnimation()
        hideAllFabs()
        observeCategoryFab()
        observeFavouritesButton()
        observeSearchButton()
        setupProductsRecyclerView()
        observeProductsResponse()
        observeButtonsGroup()
        observeCartButton()
        observeFloatingActionButton()
    }

    private fun registerNetworkManager() {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        ContextCompat.registerReceiver(
            requireActivity(),
            networkChangeListener,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onPause() {
        super.onPause()
        activity?.let {
            LocalBroadcastManager.getInstance(it).unregisterReceiver(networkChangeListener)
        }
    }

    private fun observeNetworkState() {
        NetworkManager.isNetworkAvailable.observe(viewLifecycleOwner) {
            if (it){
                viewModel.getProducts()
                handleWhenThereNetwork()
            } else {
                handleWhenNoNetwork()
            }
        }
    }

    private fun handleWhenProductsResponseError() {
        binding.apply {
            category.visibility = View.GONE
            rvProducts.visibility = View.GONE
        }
    }

    private fun handleWhenNoNetwork() {
        handleWhenProductsResponseError()
        binding.apply {
            ivNoConnection.visibility = View.VISIBLE
            tvNoConnection.visibility = View.VISIBLE
        }
    }

    private fun handleWhenThereNetwork() {
        binding.apply {
            category.visibility = View.VISIBLE
            rvProducts.visibility = View.VISIBLE
            ivNoConnection.visibility = View.GONE
            tvNoConnection.visibility = View.GONE
        }
    }

    private fun observeFavouritesButton() {
        binding.ivFavourite.setOnClickListener {
            if (sharedPreferences.getBoolean(Constants.IS_Logged, false)) {
                findNavController().navigate(CategoriesFragmentDirections.actionCategoriesFragmentToFavouritesFragment())
            } else {
                showAlertDialog()
            }
        }
    }

    private fun observeCartButton() {
        binding.ivCart.setOnClickListener {
            if (sharedPreferences.getBoolean(Constants.IS_Logged, false)) {
                findNavController().navigate(CategoriesFragmentDirections.actionCategoriesFragmentToCartFragment())
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
                    changeFabColors()
                    changeSelectedFabColor()
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
                    handleWhenProductsResponseError()
                    Utils.showErrorSnackbar(binding.root, "Error happened")
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
                R.id.btn_all -> {
                    Log.d("setOnSelectListener", "btn_all")
                    mainCategory = ALL
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

    private fun changeFabColors() {
        binding.apply {
            fabAccessories.backgroundTintList = resources.getColorStateList(R.color.white)
            fabShirt.backgroundTintList = resources.getColorStateList(R.color.white)
            fabShoes.backgroundTintList = resources.getColorStateList(R.color.white)
        }
    }

    private fun changeSelectedFabColor() {
        binding.apply {
            when (subCategory) {
                ACCESSORIES -> {
                    fabAccessories.backgroundTintList = resources.getColorStateList(R.color.orange_700)
                }
                T_SHIRTS -> {
                    fabShirt.backgroundTintList = resources.getColorStateList(R.color.orange_700)
                }
                SHOES -> {
                    fabShoes.backgroundTintList = resources.getColorStateList(R.color.orange_700)
                }
            }
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
        const val ALL = " "
        const val SHOES = "SHOES"
        const val T_SHIRTS = "T-SHIRTS"
        const val ACCESSORIES = "ACCESSORIES"
    }
}