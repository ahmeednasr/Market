package com.example.market.ui.addresses

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.market.R
import com.example.market.data.pojo.Address
import com.example.market.databinding.FragmentAccountBinding
import com.example.market.databinding.FragmentAddressesBinding
import com.example.market.ui.address_form.AddressViewModel

class AddressesFragment : Fragment() {

    private var _binding: FragmentAddressesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddressViewModel by viewModels()
    private val addressesAdaptor by lazy {
        AddressesAdaptor(object : AddressesAdaptor.AddressClickListener{
            override fun onSelectedClicked(address: Address) {

            }

            override fun onItemDeSelected(address: Address) {

            }

            override fun onItemDeleted(address: Address) {

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
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}