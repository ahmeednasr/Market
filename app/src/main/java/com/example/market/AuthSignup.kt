package com.example.market

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.market.databinding.FragmentAuthIntroBinding
import com.example.market.databinding.FragmentAuthSingupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class AuthSignup : Fragment() {

    private var _binding : FragmentAuthSingupBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth:FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAuthSingupBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth

        binding.signupButton.setOnClickListener {
            registerUser()
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun registerUser(){
        val name = binding.nameText.toString()
        val email = binding.emailText.toString()
        val password = binding.passwordText.toString()
        val rePassword = binding.rePasswordText.toString()
        binding.progressBar.visibility = View.VISIBLE
        if(validateInfo(name,email,password,rePassword)){
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    auth.currentUser!!.sendEmailVerification().addOnCompleteListener {task ->
                        if(task.isSuccessful){
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(),"Signed Up Successfully",Toast.LENGTH_LONG)
                            findNavController().navigate(R.id.action_authSignIn_to_accountFragment)
                        }else{
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(),"Please Verify Your Email",Toast.LENGTH_LONG)
                            findNavController().navigate(R.id.action_authSignIn_to_authIntro)
                        }
                    }
                }else{
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(),"Something Went Wrong, Please Try Again Later",Toast.LENGTH_LONG)
                findNavController().navigate(R.id.action_authSignIn_to_authIntro)
                }
            }
        }
    }

    private fun validateInfo(name: String, email: String, password: String, rePassword: String): Boolean {
        return when {
            TextUtils.isEmpty(name) ->{
                Toast.makeText(requireContext(),"Please Enter Your Name",Toast.LENGTH_LONG)
                false
            }
            TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->{
                Toast.makeText(requireContext(),"Please Enter a Valid Email",Toast.LENGTH_LONG)
                false
            }
            TextUtils.isEmpty(password) ->{
                Toast.makeText(requireContext(),"Please Enter Your Password",Toast.LENGTH_LONG)
                false
            }
            password != rePassword ->{
                Toast.makeText(requireContext(),"Password Not Matched",Toast.LENGTH_LONG)
                false
            }
            else -> { true }
        }
    }
}