package com.example.market

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.market.databinding.FragmentAuthSignInBinding
import com.example.market.databinding.FragmentAuthSingupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthSignIn : Fragment() {

    private var _binding : FragmentAuthSignInBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAuthSignInBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth

        binding.signInButton.setOnClickListener {
            signInUser()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun signInUser(){
        val email = binding.emailText.toString()
        val password = binding.passwordText.toString()
        binding.progressBar2.visibility = View.VISIBLE
        if(validateInfo(email,password)){
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    binding.progressBar2.visibility = View.GONE
                    Toast.makeText(requireContext(),"Login Successfully", Toast.LENGTH_LONG)
                }else{
                    binding.progressBar2.visibility = View.GONE
                    Toast.makeText(requireContext(),"Something Went Wrong,Please Try Again Later",Toast.LENGTH_LONG)
                }
            }
        }
    }

    private fun validateInfo(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->{
                Toast.makeText(requireContext(),"Please Enter a Valid Email", Toast.LENGTH_LONG)
                false
            }
            TextUtils.isEmpty(password) ->{
                Toast.makeText(requireContext(),"Please Enter Your Password", Toast.LENGTH_LONG)
                false
            }
            else -> { true }
        }
    }

}