package com.example.market.auth

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.market.ui.MainActivity
import com.example.market.R
import com.example.market.databinding.FragmentAuthSignInBinding
import com.example.market.utils.Constants
import com.example.market.utils.NetworkResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthSignIn : Fragment() {

    private var _binding : FragmentAuthSignInBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient:GoogleSignInClient

    private val viewModel:AuthViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

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
        sharedPreferences = requireContext().getSharedPreferences(Constants.SharedPreferences,0)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(),gso)

        viewModel.getAllCustomers()

        binding.signInButton.setOnClickListener {
            signInUser()
        }

        binding.googleLogin.setOnClickListener {
            signInWithGoogle()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun signInUser(){
        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()
        binding.progressBar2.visibility = View.VISIBLE
        if(validateInfo(email,password)){

            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    if(auth.currentUser!!.isEmailVerified){
                        binding.progressBar2.visibility = View.GONE
                        getUserID(email)
                        Log.i(TAG, "signInUser: ${sharedPreferences.getString(Constants.UserID,"No ID)")}")
                        Toast.makeText(requireContext(),"Login Successfully", Toast.LENGTH_LONG).show()
                        startActivity(Intent(requireActivity(), MainActivity::class.java))
                        requireActivity().finish()
                    }else{
                        binding.progressBar2.visibility = View.GONE
                        Toast.makeText(requireContext(),"Please Verify Your Email",Toast.LENGTH_LONG).show()
                    }
                }else{
                    binding.progressBar2.visibility = View.GONE
                    Toast.makeText(requireContext(),"Something Went Wrong,Please Try Again Later",Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_authSignIn_to_authIntro)
                }
            }
        }
    }

    private fun validateInfo(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->{
                Toast.makeText(requireContext(),"Please Enter a Valid Email", Toast.LENGTH_LONG).show()
                false
            }
            TextUtils.isEmpty(password) ->{
                Toast.makeText(requireContext(),"Please Enter Your Password", Toast.LENGTH_LONG).show()
                false
            }
            else -> { true }
        }
    }

    private fun signInWithGoogle(){
        val signIntent = googleSignInClient.signInIntent
        launcher.launch(signIntent)
        findNavController().navigate(AuthSignInDirections.actionAuthSignInToConfirmData())
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
        if(result.resultCode==Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResult(task)
        }

    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account:GoogleSignInAccount? = task.result
            if(account!=null){
                updateUI(account)
            }
        }else{
            Toast.makeText(requireContext(),"SignIn Failed, Try Again Later",Toast.LENGTH_LONG).show()
        }

    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){
                binding.progressBar2.visibility = View.GONE
                Toast.makeText(requireContext(),"Login Successfully", Toast.LENGTH_LONG).show()
                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }else{
                binding.progressBar2.visibility = View.GONE
                Toast.makeText(requireContext(),"Something Went Wrong,Please Try Again Later",Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_authSignIn_to_authIntro)
            }
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