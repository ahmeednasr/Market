package com.example.market.ui.addresses

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.market.R
import com.example.market.data.pojo.CustomerAddress
import com.example.market.databinding.FragmentAddressesBinding
import com.example.market.ui.address_form.AddressViewModel
import com.example.market.utils.NetworkResult
import com.example.market.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressesFragment : Fragment() {

    private var _binding: FragmentAddressesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddressViewModel by viewModels()
    private val addressesAdaptor by lazy {
        AddressesAdaptor(object : AddressesAdaptor.AddressClickListener{
            override fun onSelectedClicked(address: CustomerAddress) {
                viewModel.setDefaultAddress(address)
                getAddressesList()
            }

            override fun onItemDeSelected(address: CustomerAddress) {
                Toast.makeText(requireContext(),"Default Location Has Been Changed",Toast.LENGTH_SHORT).show()
            }

            override fun onItemDeleted(address: CustomerAddress) {
                viewModel.deleteAddress(address)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddressesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_addressesFragment_to_addressFormFragment)
        }
        binding.ivBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.getCustomerAddresses()
        getAddressesList()
        binding.rvAddresses.apply {
            adapter = addressesAdaptor
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        }
    }

    private fun getAddressesList(){
        viewModel.costumer.observe(viewLifecycleOwner){response ->
            when(response){
                is NetworkResult.Success -> {
                    response.data?.let {
                        addressesAdaptor.submitList(it.customer.addresses)
                    }
                }
                is NetworkResult.Error -> {
                    Utils.showErrorSnackbar(binding.root, "Error happened")
                }
                is NetworkResult.Loading -> {
                    Toast.makeText(requireContext(),"Loading Data",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}