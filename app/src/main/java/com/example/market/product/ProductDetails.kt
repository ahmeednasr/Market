package com.example.market.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.navArgs
import com.denzcoskun.imageslider.models.SlideModel
import com.example.market.R
import com.example.market.data.pojo.Product
import com.example.market.databinding.FragmentProductDetailsBinding

class ProductDetails : Fragment() {

    private var _binding : FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!

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
        setUI(args.product)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setColorList(colorList : ArrayList<String>): String{
        var itemSelected = ""
        val colorAutoComplete = binding.autoCompleteColor
        val colorAdapter = ArrayAdapter(requireContext(), R.layout.list_item, colorList)
        colorAutoComplete.setAdapter(colorAdapter)

        colorAutoComplete.onItemClickListener = AdapterView.OnItemClickListener{
                adapterView, view, i,l ->
            itemSelected = adapterView.getItemAtPosition(i).toString()
        }
        return itemSelected
    }

    private fun setSizeList(sizeList : ArrayList<String>) : String{
        var itemSelected = ""
        val sizeAutoComplete = binding.autoCompleteSize
        val sizeAdapter = ArrayAdapter(requireContext(), R.layout.list_item, sizeList)
        sizeAutoComplete.setAdapter(sizeAdapter)

        sizeAutoComplete.onItemClickListener = AdapterView.OnItemClickListener{
                adapterView, view, i,l ->
            itemSelected = adapterView.getItemAtPosition(i).toString()
        }
        return itemSelected
    }

    private fun setUI(product: Product){
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
        val variant = "\"" + setColorList(colorList) + " / "+setSizeList(sizeList) + "\""

        for (i in product.variants!!.indices){
            if(product.variants[i].title == variant){
                binding.priceText.text = product.variants[i].price
                binding.availability.text = product.variants[i].inventory_quantity.toString()
            }
        }

    }

}