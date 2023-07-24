package com.example.market.auth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.market.R
import com.example.market.data.pojo.NewUser
import com.example.market.data.pojo.User
import com.example.market.databinding.FragmentAuthSingupBinding
import com.example.market.databinding.FragmentConfirmDataBinding
import com.example.market.ui.MainActivity
import com.example.market.utils.Constants
import com.example.market.utils.NetworkResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ConfirmData : Fragment() {

    private var _binding : FragmentConfirmDataBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var user: User

    @Inject
    lateinit var sharedPreferences:SharedPreferences

    private val viewModel:AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentConfirmDataBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        binding.confirmButton.setOnClickListener {
            confirmUser()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun confirmUser(){
        val firstName = binding.firstNameText.text.toString()
        val lastName = binding.lastNameText.text.toString()
        val phone = binding.phoneText.text.toString()
        val email = binding.emailText.text.toString()
        if(validateInfo(firstName,lastName,phone,email)){
            val newUser = NewUser(user)
            viewModel.createUser(newUser)
            getUserID(email)
            Toast.makeText(requireContext(),"Login Successfully", Toast.LENGTH_LONG).show()
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun validateInfo(firstName: String,lastName:String,phone:String, email: String) : Boolean {
        return when {
            TextUtils.isEmpty(firstName) ->{
                Toast.makeText(requireContext(),"Please Enter Your First Name", Toast.LENGTH_LONG).show()
                false
            }
            TextUtils.isEmpty(lastName) ->{
                Toast.makeText(requireContext(),"Please Enter Your Last Name", Toast.LENGTH_LONG).show()
                false
            }
            TextUtils.isEmpty(phone) ->{
                Toast.makeText(requireContext(),"Please Enter Your Phone Number", Toast.LENGTH_LONG).show()
                false
            }
            TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->{
                Toast.makeText(requireContext(),"Please Enter a Valid Email", Toast.LENGTH_LONG).show()
                false
            }
            else -> { true }
        }
    }

    private fun getUserID(email:String){
        val editor = sharedPreferences.edit()
        viewModel.customers.observe(viewLifecycleOwner){response ->
            when(response){
                is NetworkResult.Success -> {
                    response.data?.let {
                        for(customer in it){
                            if (email==customer.email){
                                editor.putString(Constants.UserID, customer.id.toString())
                                editor.putString(Constants.FAVOURITE_ID, customer.note.toString())
                                editor.putString(Constants.CART_ID, customer.multipass_identifier.toString())
                                editor.putBoolean(Constants.IS_Logged, true)
                                editor.apply()
                            }
                        }
                    }
                }
                is NetworkResult.Error -> {

                }
                is NetworkResult.Loading -> {

                }
                else -> {}
            }

        }

    }

}