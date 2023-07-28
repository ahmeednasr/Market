package com.example.market.ui.payment

import android.content.SharedPreferences
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
import com.example.market.utils.Constants
import com.example.market.utils.Constants.CASH_ON_DELIVERY
import com.example.market.utils.Constants.MAX_CASH_ON_DELIVERY
import com.example.market.utils.Constants.ONLINE_PAYMENT
import com.example.market.utils.Constants.USD_VALUE
import com.example.market.utils.NetworkResult
import com.example.market.utils.Utils
import com.example.market.utils.Utils.roundOffDecimal
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
import javax.inject.Inject

@AndroidEntryPoint
class PaymentFragment : Fragment() {
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!
    val viewModel: PaymentViewModel by viewModels()
    private var paymentMethod: String = CASH_ON_DELIVERY

    @Inject
    lateinit var sharedPreferences: SharedPreferences
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
        viewModel.getCartItems()
        viewModel.convertCurrency()
        viewModel.getUSDExchange()
        setUpPayPal()
        observeDiscount()
        Log.d("menp", "${sharedPreferences.getFloat(USD_VALUE, .0f)}")
        observeCost()
        binding.currencyName.text =
            sharedPreferences.getString(Constants.CURRENCY_TO_KEY, "") ?: "EGP"
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
                    binding.discountValue.text = ("0")
                    viewModel.addDiscountToView(0.0)
                    updateUI(0.0)
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
                        var discountPercentage = result.data.value!!.substring(1) + "%"
                        viewModel.addDiscountToView(result.data.value.toDouble())
                        binding.discountValue.text = (discountPercentage)
                        var value = result.data.value.toDouble()
                        updateUI(value)

                    } else {
                        binding.textInputLayout2.boxStrokeColor = resources.getColor(R.color.red)
                        binding.textInputLayout2.hint = getString(R.string.inValid_Coupon)
                        binding.textInputLayout2.hintTextColor =
                            ColorStateList.valueOf(resources.getColor(R.color.red))
                        binding.discountValue.text = ("0")
                        viewModel.addDiscountToView(0.0)
                        updateUI(0.0)
                    }
                }
                is NetworkResult.Loading -> {
                    binding.textInputLayout2.hint = getString(R.string.coupon)

                    binding.textInputLayout2.boxStrokeColor = resources.getColor(R.color.ash_gray)
                    binding.textInputLayout2.hintTextColor =
                        ColorStateList.valueOf(resources.getColor(R.color.ash_gray))
                    binding.discountValue.text = ("0")
                    viewModel.addDiscountToView(0.0)
                    updateUI(0.0)
                }
                is NetworkResult.Error -> {
                    binding.textInputLayout2.hint = getString(R.string.inValid_Coupon)
                    binding.textInputLayout2.boxStrokeColor = resources.getColor(R.color.red)
                    binding.textInputLayout2.hintTextColor =
                        ColorStateList.valueOf(resources.getColor(R.color.red))
                    binding.discountValue.text = ("0")
                    viewModel.addDiscountToView(0.0)
                }
            }
        }
    }

    private fun observeCost() {
        viewModel.cart.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Success -> {
                    var exchange = (sharedPreferences.getFloat(
                        Constants.Exchange_Value, 1.0f
                    ))
                    binding.subTotalValue.text =
                        roundOffDecimal((((result.data?.draftOrder?.subtotalPrice)!!.toDouble()) * exchange)).toString()
                    binding.TaxValue.text =
                        roundOffDecimal((((result.data.draftOrder.totalTax)!!.toDouble()) * exchange)).toString()
                    binding.totalValue.text =
                        roundOffDecimal((((result.data.draftOrder.totalPrice)!!.toDouble()) * exchange)).toString()
                    val total =
                        roundOffDecimal(((result.data.draftOrder.totalPrice).toDouble()))
                    if (total >= MAX_CASH_ON_DELIVERY) {
                        paymentMethod = ONLINE_PAYMENT
                        binding.paymentButtonContainer.visibility = View.VISIBLE
                        binding.nextBtn.visibility = View.INVISIBLE
                        binding.onlinePayment.isChecked = true
                        binding.onlinePayment.isClickable = false
                        binding.cashOnDelivery.isChecked = false
                        binding.cashOnDelivery.isClickable = false
                        Toast.makeText(
                            requireContext(),
                            "reach to limit in cashOnDelivery",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        binding.nextBtn.visibility = View.VISIBLE
                    }
                }
                is NetworkResult.Loading -> {

                }
                is NetworkResult.Error -> {

                }
            }
        }
    }

    private fun setUpPayPal() {
        val current = binding.totalValue.text.toString().toDouble()
        val value = sharedPreferences.getFloat(USD_VALUE, .0f).toDouble()
        Log.d("menp", "curr=$current")
        Log.d("menp", "value=$value")
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
                                Amount(
                                    currencyCode = CurrencyCode.USD,
                                    value = "10.20"
                                    // roundOffDecimal(current * value).toString()
                                )
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

    private fun updateUI(value: Double) {
        var subTotal = binding.subTotalValue.text.toString().toDouble()
        var totalTax = binding.TaxValue.text.toString().toDouble()
        subTotal += (subTotal * (value / 100))
        var totalPrice = (subTotal + totalTax)
        binding.totalValue.text = roundOffDecimal(totalPrice).toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}



