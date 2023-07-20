package com.example.market.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.market.auth.AuthActivity
import com.example.market.databinding.FragmentAccountBinding
import com.example.market.ui.account.AccountFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    lateinit var auth:FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        observeSearchButton()
        checkUser()


    }

    private fun observeSearchButton() {
        binding.ivSearch.setOnClickListener {
            findNavController().navigate(AccountFragmentDirections.actionAccountFragmentToSearchFragment())
        }
    }
    private fun checkUser(){
        val user = auth.currentUser
        if (user != null){
            if(user.isEmailVerified){
                binding.tvLogin.text = "Logout"
                binding.tvUsername.text = user.displayName
                binding.tvLogin.setOnClickListener {
                    Firebase.auth.signOut()
                 }
            }else{
                binding.tvLogin.setOnClickListener {
                    startActivity(Intent(requireActivity(), AuthActivity::class.java))
                }
            }
        }else{
            binding.tvLogin.setOnClickListener {
                startActivity(Intent(requireActivity(), AuthActivity::class.java))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}