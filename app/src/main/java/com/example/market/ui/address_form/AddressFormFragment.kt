package com.example.market.ui.address_form

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.SpannableStringBuilder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.market.R
import com.example.market.data.pojo.StatesItem
import com.example.market.data.pojo.UserAddress
import com.example.market.databinding.FragmentAddressFormBinding
import com.example.market.databinding.FragmentCartBinding
import com.example.market.utils.Constants
import com.example.market.utils.Constants.ADDRESS_KEY
import com.example.market.utils.Constants.MAP
import com.example.market.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressFormFragment : Fragment() {
    private var _binding: FragmentAddressFormBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddressViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddressFormBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getGovernments("egypt")

        binding.saveBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.locationBtn.setOnClickListener {
            findNavController().navigate(R.id.action_addressFormFragment_to_mapFragment)
        }
        setFragmentResultListener(MAP) { _, result ->
            val address = UserAddress(
                result.getString(ADDRESS_KEY),
                result.getString(Constants.CITY_KEY),
                result.getString(Constants.GOVERN_KEY),
                null,
                result.getString(Constants.POSTAL_KEY),
                null,
                null,
                "EG"
            )
            binding.tiAddress.text = SpannableStringBuilder(address?.address1)
            binding.itZipcode.text = SpannableStringBuilder(address?.zip)
            binding.autoCompleteGovern.text = SpannableStringBuilder(address?.province)
            binding.autoCompleteCity.text = SpannableStringBuilder(address?.city)
            binding.itPhone.inputType = InputType.TYPE_CLASS_PHONE
            binding.saveBtn.visibility = View.VISIBLE
        }
        observeGovernmentResponse()
        observeCitiesResponse()
    }

    private fun setGovernmentList(governments: List<StatesItem?>?): String {
        var itemSelected = ""
        val governAutoComplete = binding.autoCompleteGovern
        var governNames = mutableListOf<String>()
        for (g in governments!!) {
            g?.name?.let { governNames.add(it) }
        }
        val governAdapter = ArrayAdapter(requireContext(), R.layout.list_item, governNames)
        governAutoComplete.setAdapter(governAdapter)
        governAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, i, l ->
                itemSelected = adapterView.getItemAtPosition(i).toString()
                setCityList(listOf())
                viewModel.getCities("egypt", itemSelected)
            }
        return itemSelected
    }

    private fun setCityList(cityList: List<String?>?): String {
        var itemSelected = ""
        val cityAutoComplete = binding.autoCompleteCity
        val cityAdapter = ArrayAdapter(requireContext(), R.layout.list_item, cityList!!)
        cityAutoComplete.setAdapter(cityAdapter)
        cityAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, i, l ->
                itemSelected = adapterView.getItemAtPosition(i).toString()
            }
        return itemSelected
    }

    private fun observeGovernmentResponse() {
        viewModel.governmentsResult.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let {
                        setGovernmentList(it.data?.states)
                    }
                }
                is NetworkResult.Error -> {
                }
                is NetworkResult.Loading -> {
                }
            }
        }
    }

    private fun observeCitiesResponse() {
        viewModel.citiesResult.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let {
                        setCityList(it.data)
                    }
                }
                is NetworkResult.Error -> {
                }
                is NetworkResult.Loading -> {
                }
            }
        }
    }

    private fun enableEditing() {
        binding.locationBtn.isEnabled = true
        binding.locationBtn.isClickable = true
        binding.tiAddress.inputType = InputType.TYPE_CLASS_TEXT
        binding.tiAddress.isEnabled = true
        binding.itZipcode.inputType = InputType.TYPE_CLASS_NUMBER
        binding.itZipcode.isEnabled = true
        binding.itPhone.inputType = InputType.TYPE_CLASS_PHONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

    }
}