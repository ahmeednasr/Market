package com.example.market.auth

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.market.R
import com.example.market.data.pojo.User
import com.example.market.data.pojo.NewUser
import com.example.market.databinding.FragmentAuthSingupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthSignup : Fragment() {

    private var _binding : FragmentAuthSingupBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth:FirebaseAuth
    private lateinit var user: User
    private val viewModel:AuthViewModel by viewModels()

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
        val firstName = binding.firstNameText.text.toString()
        val lastName = binding.lastNameText.text.toString()
        val phone = binding.phoneText.text.toString()
        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()
        val rePassword = binding.rePasswordText.text.toString()
        binding.progressBar.visibility = View.VISIBLE
        if(validateInfo(firstName,lastName,phone,email,password,rePassword)){
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    auth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(),"Signed Up Successfully, Please Verify Your Email",Toast.LENGTH_LONG).show()
                        user = User(firstName,lastName,email,phone,true,null,null)
                        val newUser = NewUser(user)
                        viewModel.createUser(newUser)
                        findNavController().navigate(R.id.action_authSignup_to_authIntro)
                    }?.addOnFailureListener {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(),"Something Went Wrong, Please Try Again Later",Toast.LENGTH_LONG).show()
                        findNavController().navigate(R.id.action_authSignup_to_authIntro)
                    }
                }else{
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(),"Something Went Wrong, Please Try Again Later",Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_authSignup_to_authIntro)
                }
            }
        }
    }

    private fun validateInfo(firstName: String,lastName:String,phone:String, email: String, password: String, rePassword: String): Boolean {
        return when {
            TextUtils.isEmpty(firstName) ->{
                Toast.makeText(requireContext(),"Please Enter Your First Name",Toast.LENGTH_LONG).show()
                false
            }
            TextUtils.isEmpty(lastName) ->{
                Toast.makeText(requireContext(),"Please Enter Your Last Name",Toast.LENGTH_LONG).show()
                false
            }
            TextUtils.isEmpty(phone) ->{
                Toast.makeText(requireContext(),"Please Enter Your Phone Number",Toast.LENGTH_LONG).show()
                false
            }
            TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->{
                Toast.makeText(requireContext(),"Please Enter a Valid Email",Toast.LENGTH_LONG).show()
                false
            }
            TextUtils.isEmpty(password) ->{
                Toast.makeText(requireContext(),"Please Enter Your Password",Toast.LENGTH_LONG).show()
                false
            }
            password != rePassword ->{
                Toast.makeText(requireContext(),"Password Not Matched",Toast.LENGTH_LONG).show()
                false
            }
            else -> { true }
        }
    }
}