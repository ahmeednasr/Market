package com.example.market.ui.product

import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.denzcoskun.imageslider.models.SlideModel
import com.example.market.R
import com.example.market.data.pojo.Product
import com.example.market.databinding.FragmentProductDetailsBinding
import com.example.market.utils.Constants
import com.example.market.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt
import kotlin.random.Random

@AndroidEntryPoint
class ProductDetails : Fragment() {

    private var _binding : FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val viewModel : ProductDetailsViewModel by viewModels()

    private val args : ProductDetailsArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProductDetailsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences(Constants.SharedPreferences, 0)
        editor = sharedPreferences.edit()

        viewModel.getProduct(args.productId)
        viewModel.getProducts()

        observeProductResponse()


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setColorList(colorList : ArrayList<String>){
        var itemSelected = ""
        val colorAutoComplete = binding.autoCompleteColor
        val colorAdapter = ArrayAdapter(requireContext(), R.layout.list_item, colorList)
        colorAutoComplete.setAdapter(colorAdapter)

        colorAutoComplete.onItemClickListener = AdapterView.OnItemClickListener{
                adapterView, view, i,l ->
            itemSelected = adapterView.getItemAtPosition(i).toString()
        }
    }

    private fun setSizeList(sizeList : ArrayList<String>,product: Product) {
        var itemSelected = ""
        val sizeAutoComplete = binding.autoCompleteSize
        val sizeAdapter = ArrayAdapter(requireContext(), R.layout.list_item, sizeList)
        sizeAutoComplete.setAdapter(sizeAdapter)

        sizeAutoComplete.onItemClickListener = AdapterView.OnItemClickListener{
                adapterView, view, i,l ->
            itemSelected = adapterView.getItemAtPosition(i).toString()
            binding.availability.visibility = View.VISIBLE
            for(i in product.variants!!.indices){
                if (itemSelected == product.variants!![i].option1){
                    binding.availability.text = getString(R.string.availability)+" "+product.variants!![i].inventory_quantity.toString()
                    binding.availability.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setUI(product: Product){
        val random = Random
        val randomDouble = random.nextDouble() * 4 + 1
        val randomRounded = (randomDouble * 2).roundToInt() / 2.0

        binding.availability.visibility = View.GONE
        val imageList = ArrayList<SlideModel>()
        val imgSlider = binding.imageSlider
        for(i in product.images?.indices!!){
            imageList.add(SlideModel(product.images[i].src))
        }
        imgSlider.setImageList(imageList)
        binding.titleText.text = product.title
        binding.brandText.text = product.vendor
        binding.descreptionText.text = product.body_html
        val sizeList = ArrayList<String>()
        for(i in product.options!![0].values.indices){
            sizeList.add(product.options[0].values[i])
        }
        val colorList = ArrayList<String>()
        for(i in product.options[1].values.indices){
            colorList.add(product.options[1].values[i])
        }
        setColorList(colorList)
        setSizeList(sizeList,product)
        binding.priceText.text = product.variants!![0].price
        binding.ratingBar.rating = randomRounded.toFloat()
        checkFavorite(product)

    }

    private fun checkFavorite(product: Product){
        if (product.isFavourite){
            binding.addToFavoriteButton.setImageDrawable(binding.root.context.getDrawable(R.drawable.ic_filled_heart))
        }
        binding.addToFavoriteButton.setOnClickListener {
            if (product.isFavourite) {
                viewModel.deleteFavourite(product)
                binding.addToFavoriteButton.setImageDrawable(binding.root.context.getDrawable(R.drawable.ic_heart))
            } else {
                viewModel.addFavourite(product)
                binding.addToFavoriteButton.setImageDrawable(binding.root.context.getDrawable(R.drawable.ic_filled_heart))
            }
            product.isFavourite = !product.isFavourite
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
    private fun observeProductsResponse() {
        viewModel.products.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    //stopShimmer()
                    response.data?.let {
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

}