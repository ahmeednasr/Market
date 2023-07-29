package com.example.market.ui.address_form

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
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
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.market.R
import com.example.market.data.pojo.*
import com.example.market.databinding.FragmentAddressFormBinding
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
//            createAddresses()
//            findNavController().popBackStack()
            val validation = validationInput()
            if (validation) {
                createAddresses()
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), R.string.data_not_valid, Toast.LENGTH_SHORT).show()
            }
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

    private fun createAddresses() {
        val city = binding.autoCompleteCity.text.toString()
        val country = binding.itCountry.text.toString()
        val province = binding.autoCompleteGovern.text.toString().split(" ")[0]
        val zip = binding.itZipcode.text.toString()
        val address1 = binding.tiAddress.text.toString()
        val phone = binding.itPhone.text.toString()
        val address = CustomerAddress(
            country = "Egypt",
            province = province,
            city = city,
            phone = phone,
            zip = zip,
            address1 = address1
        )
        Log.d("ADDRESS", "address $address")

        val update = CustomerResponse(Customer(addresses = listOf(address)))
        Log.d("ADDRESS", update.toString())

        viewModel.modifyCustomerAddress(update)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun validationInput(): Boolean {
        val addressIsEmpty = binding.tiAddress.text?.isNotBlank()!!
        val zipCodeIsEmpty = binding.itZipcode.text?.isNotBlank()!!
        val governmentIsEmpty = binding.autoCompleteGovern.text?.isNotBlank()!!
        val cityIsEmpty = binding.autoCompleteCity.text?.isNotBlank()!!
        val phonePattern = Regex("""^01\d{9}$""")
        val phoneIsValid = phonePattern.matches(binding.itPhone.text.toString())
        val validation =
            addressIsEmpty && zipCodeIsEmpty && governmentIsEmpty && cityIsEmpty && phoneIsValid
        return validation
    }

}