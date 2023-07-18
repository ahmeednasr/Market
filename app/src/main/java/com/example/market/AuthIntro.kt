package com.example.market

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.market.databinding.FragmentAuthIntroBinding

class AuthIntro : Fragment() {

    private var _binding : FragmentAuthIntroBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAuthIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signupButton.setOnClickListener {
            findNavController().navigate(R.id.action_authIntro_to_authSignup)

        }
        binding.signInButton.setOnClickListener {
            findNavController().navigate(R.id.action_authIntro_to_authSignIn)

        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}