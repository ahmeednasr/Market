package com.example.market.ui.payment

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.market.R
import com.example.market.databinding.FragmentPaymentBinding
import com.example.market.utils.Constants.CASH_ON_DELIVERY
import com.example.market.utils.Constants.ONLINE_PAYMENT
import com.example.market.utils.NetworkResult
import com.paypal.checkout.approve.OnApprove
import com.paypal.checkout.cancel.OnCancel
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.error.OnError
import com.paypal.checkout.order.Amount
import com.paypal.checkout.order.AppContext
import com.paypal.checkout.order.OrderRequest
import com.paypal.checkout.order.PurchaseUnit
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentFragment : Fragment() {
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!
    val viewModel: PaymentViewModel by viewModels()
    private var paymentMethod: String = CASH_ON_DELIVERY

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
        setUpPayPal()
        observeDiscount()
        binding.onlinePayment.setOnClickListener {
            paymentMethod = ONLINE_PAYMENT
            binding.paymentButtonContainer.visibility = View.VISIBLE
            binding.nextBtn.visibility = View.INVISIBLE
        }
        binding.cashOnDelivery.setOnClickListener {
            paymentMethod = CASH_ON_DELIVERY
            binding.paymentButtonContainer.visibility = View.INVISIBLE
            binding.nextBtn.visibility = View.VISIBLE
        }

        binding.coupon.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("TESTES", s.toString())
                if (s.toString().isEmpty()) {
                    binding.textInputLayout2.hint = getString(R.string.coupon)

                    binding.textInputLayout2.boxStrokeColor = resources.getColor(R.color.orange)
                    binding.textInputLayout2.hintTextColor =
                        ColorStateList.valueOf(resources.getColor(R.color.orange))
                } else {
                    viewModel.getDiscountCodes(s.toString())
                }

            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun observeDiscount() {
        viewModel.discountCodes.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Success -> {
                    if (result.data != null) {
                        binding.textInputLayout2.boxStrokeColor = resources.getColor(R.color.green)
                        binding.textInputLayout2.hint = getString(R.string.Valid_Coupon)
                        binding.textInputLayout2.hintTextColor =
                            ColorStateList.valueOf(resources.getColor(R.color.green))
                    } else {
                        binding.textInputLayout2.boxStrokeColor = resources.getColor(R.color.red)
                        binding.textInputLayout2.hint = getString(R.string.inValid_Coupon)
                        binding.textInputLayout2.hintTextColor =
                            ColorStateList.valueOf(resources.getColor(R.color.red))
                    }
                }
                is NetworkResult.Loading -> {
                    binding.textInputLayout2.hint = getString(R.string.coupon)

                    binding.textInputLayout2.boxStrokeColor = resources.getColor(R.color.ash_gray)
                    binding.textInputLayout2.hintTextColor =
                        ColorStateList.valueOf(resources.getColor(R.color.ash_gray))
                }
                is NetworkResult.Error -> {
                    binding.textInputLayout2.hint = getString(R.string.inValid_Coupon)
                    binding.textInputLayout2.boxStrokeColor = resources.getColor(R.color.red)
                    binding.textInputLayout2.hintTextColor =
                        ColorStateList.valueOf(resources.getColor(R.color.red))
                }
            }
        }
    }

    private fun setUpPayPal() {
        binding.paymentButtonContainer.setup(
            createOrder =
            CreateOrder { createOrderActions ->
                val order =
                    OrderRequest(
                        intent = OrderIntent.CAPTURE,
                        appContext = AppContext(userAction = UserAction.PAY_NOW),
                        purchaseUnitList =
                        listOf(
                            PurchaseUnit(
                                amount =
                                Amount(currencyCode = CurrencyCode.USD, value = "10.00")
                            )
                        )
                    )
                createOrderActions.create(order)
            },
            onApprove =
            OnApprove { approval ->
                Toast.makeText(requireContext(), "payment approve", Toast.LENGTH_SHORT).show()
            },
            onCancel = OnCancel {
                Toast.makeText(requireContext(), "payment cancel", Toast.LENGTH_SHORT).show()
            },
            onError = OnError { errorInfo ->
                Toast.makeText(requireContext(), "payment error", Toast.LENGTH_SHORT).show()
            },
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}



