package com.example.market.ui.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.market.R
import com.example.market.databinding.FragmentCartBinding
import com.example.market.databinding.FragmentHomeBinding
import com.example.market.databinding.FragmentPaymentBinding


class PaymentFragment : Fragment() {
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.nextBtn.setOnClickListener {
            findNavController().navigate(PaymentFragmentDirections.actionPaymentFragmentToOrdersFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}