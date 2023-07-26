package com.example.market.ui.product

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.models.SlideModel
import com.example.market.R
import com.example.market.auth.AuthActivity
import com.example.market.data.pojo.Product
import com.example.market.databinding.FragmentProductDetailsBinding
import com.example.market.utils.Constants
import com.example.market.utils.Constants.UserID
import com.example.market.utils.NetworkResult
import com.example.market.utils.Utils.roundOffDecimal
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.random.Random

@AndroidEntryPoint
class ProductDetails : Fragment() {

    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!
    var variantId: Long = 0
    var quantity: Int = 0

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private val viewModel: ProductDetailsViewModel by viewModels()
    private val reviewAdaptor by lazy { ReviewAdaptor() }
    var reviewsList = listOf<UserReview>(
        UserReview("John Doe", "Good product with high quality."),
        UserReview("Mahmoud Ism", "As expected to be."), UserReview(
            "Mo Ali",
            "The product is good but the delivery was bit slow."
        )
    )
    private var reviewShow = true

    private val args: ProductDetailsArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getProduct(args.productId)
        observeProductResponse()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setColorList(colorList: ArrayList<String>) {
        var itemSelected = ""
        val colorAutoComplete = binding.autoCompleteColor
        val colorAdapter = ArrayAdapter(requireContext(), R.layout.list_item, colorList)
        colorAutoComplete.setAdapter(colorAdapter)

        colorAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, i, l ->
                itemSelected = adapterView.getItemAtPosition(i).toString()
            }
    }

    private fun setSizeList(sizeList: ArrayList<String>, product: Product) {
        var itemSelected = ""
        val sizeAutoComplete = binding.autoCompleteSize
        val sizeAdapter = ArrayAdapter(requireContext(), R.layout.list_item, sizeList)
        sizeAutoComplete.setAdapter(sizeAdapter)

        sizeAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, i, l ->
                itemSelected = adapterView.getItemAtPosition(i).toString()
                binding.availability.visibility = View.VISIBLE
                for (i in product.variants!!.indices) {
                    if (itemSelected == product.variants[i].option1) {
                        variantId = product.variants[i].id!!
                        quantity = product.variants[i].inventory_quantity!!
                        binding.availability.text =
                            getString(R.string.availability) + " " + product.variants!![i].inventory_quantity.toString()
                        binding.availability.visibility = View.VISIBLE
                    }
                }
            }
    }

    private fun setUI(product: Product) {
        val random = Random
        val randomDouble = random.nextDouble() * 4 + 1
        val randomRounded = (randomDouble * 2).roundToInt() / 2.0
        quantity = 0
        binding.availability.visibility = View.GONE
        val imageList = ArrayList<SlideModel>()
        val imgSlider = binding.imageSlider
        for (i in product.images?.indices!!) {
            imageList.add(SlideModel(product.images[i].src))
        }
        imgSlider.setImageList(imageList)
        binding.titleText.text = product.title
        binding.brandText.text = product.vendor
        binding.descriptionText.text = product.body_html
        val sizeList = ArrayList<String>()
        for (i in product.options!![0].values.indices) {
            sizeList.add(product.options[0].values[i])
        }
        val colorList = ArrayList<String>()
        for (i in product.options[1].values.indices) {
            colorList.add(product.options[1].values[i])
        }
        setColorList(colorList)
        setSizeList(sizeList, product)
        viewModel.conversionResult.observe(viewLifecycleOwner) {
            Log.i("STRING", "${product.variants!![0].price}")
            val currency =
                product.variants[0].price?.toDouble()?.times(it)
                    ?.let { it1 -> roundOffDecimal(it1).toString() } + " " + sharedPreferences.getString(
                    Constants.CURRENCY_TO_KEY,
                    ""
                )
            binding.priceText.text = currency
        }

        binding.ratingBar.rating = randomRounded.toFloat()
        checkFavorite(product)
        reviewAdaptor.submitList(reviewsList)
        setupReviewRecyclerView()
        binding.reviewCard.setOnClickListener {
            if (reviewShow) {
                binding.revRV.visibility = View.VISIBLE
                reviewShow = false
            } else {
                binding.revRV.visibility = View.GONE
                reviewShow = true
            }
        }
        binding.addToChartButton.setOnClickListener {
            Log.i("CART", "$quantity $variantId")
            if (quantity > 0 && variantId > 0) {
                viewModel.saveToCart(product, variantId)
            } else {
                Toast.makeText(requireContext(), "size and color not selected", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    private fun checkFavorite(product: Product) {
        if (product.isFavourite) {
            binding.addToFavoriteButton.setImageDrawable(binding.root.context.getDrawable(R.drawable.ic_filled_heart))
        }
        binding.addToFavoriteButton.setOnClickListener {
            if (sharedPreferences.getBoolean(Constants.IS_Logged, false)) {
                if (product.isFavourite) {
                    viewModel.deleteFavourite(product)
                    binding.addToFavoriteButton.setImageDrawable(binding.root.context.getDrawable(R.drawable.ic_heart))
                } else {
                    viewModel.addFavourite(product)
                    binding.addToFavoriteButton.setImageDrawable(binding.root.context.getDrawable(R.drawable.ic_filled_heart))
                }
                product.isFavourite = !product.isFavourite
            } else {
                showAlertDialog()
            }
        }
    }

    private fun observeProductResponse() {
        viewModel.product.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    //stopShimmer()
                    response.data?.let {
                        setUI(it.product!!)
                    }
                }
                is NetworkResult.Error -> {
                    //stopShimmer()
                }
                is NetworkResult.Loading -> {
                    //startShimmer()
                }
            }
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        builder.setMessage("You Must login first.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
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

    private fun setupReviewRecyclerView() {
        binding.revRV.apply {
            adapter = reviewAdaptor
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

}