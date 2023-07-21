package com.example.market.ui.address_form

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.example.market.R
import com.example.market.databinding.FragmentAddressFormBinding
import com.example.market.databinding.FragmentCartBinding
import com.example.market.utils.Constants.MAP

class AddressFormFragment : Fragment() {
    private var _binding: FragmentAddressFormBinding? = null
    private val binding get() = _binding!!

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
        binding.locationBtn.isEnabled = false
        binding.autoCompleteCity.isEnabled = false
        binding.saveBtn.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.locationBtn.setOnClickListener {
            findNavController().navigate(R.id.action_addressFormFragment_to_mapFragment)
        }
        setCityList(arrayListOf("alexandria", "cairo", "aswan"))
        setFragmentResultListener(MAP) { _, result ->
            //binding.
        }
        binding.changeBtn.setOnClickListener {
            binding.saveBtn.visibility = View.VISIBLE
            enableEditing()
        }
    }

    private fun setCityList(cityList: ArrayList<String>): String {
        var itemSelected = ""
        val cityAutoComplete = binding.autoCompleteCity
        val cityAdapter = ArrayAdapter(requireContext(), R.layout.list_item, cityList)
        cityAutoComplete.setAdapter(cityAdapter)
        cityAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, i, l ->
                itemSelected = adapterView.getItemAtPosition(i).toString()
            }
        return itemSelected
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun enableEditing() {
        binding.locationBtn.isEnabled = true
        binding.locationBtn.isClickable = true
        binding.tiAddress.inputType = InputType.TYPE_CLASS_TEXT
        binding.tiAddress.isEnabled = true
        binding.itZipcode.inputType = InputType.TYPE_CLASS_NUMBER
        binding.itZipcode.isEnabled = true
        binding.itPhone.inputType = InputType.TYPE_CLASS_PHONE
    }
}