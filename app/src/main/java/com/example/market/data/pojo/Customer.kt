package com.example.market.data.pojo

import com.google.gson.annotations.SerializedName

import java.io.Serializable

data class CustomerResponse(
    val customer: Customer
)

data class CustomersResponse(
    val customers: List<Customer>
)

data class NewUser(
    val customer: User
)

data class User(
    val first_name: String,
    val last_name: String?,
    val email: String,
    val phone: String?,
    val verified_email: Boolean,
    val addresses: List<UserAddress>?,
    val send_email_invite: Boolean?,
    @SerializedName("note")
    var favouriteId: String? = "",
    @SerializedName("multipass_identifier")
    var cartId: String? = ""
)

data class UserAddress(
    val address1: String?,
    val city: String?,
    val province: String?,
    val phone: String?,
    val zip: String?,
    val last_name: String?,
    val first_name: String?,
    val country: String?
) : Serializable

data class Customer(
    val id: Long? = null,
    val email: String? = null,
    val accepts_marketing: Boolean? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val orders_count: Int? = null,
    val state: String? = null,
    val total_spent: String? = null,
    val last_order_id: Long? = null,
    var note: String? = null,
    val verified_email: Boolean? = null,
    var multipass_identifier: String? = null,
    val tax_exempt: Boolean? = null,
    val tags: String? = null,
    val last_order_name: String? = null,
    val currency: String? = null,
    val phone: String? = null,
    val addresses: List<CustomerAddress>? = null,
    val accepts_marketing_updated_at: String? = null,
    val marketing_opt_in_level: String? = null,
    val tax_exemptions: List<String>? = null,
    val email_marketing_consent: EmailMarketingConsent? = null,
    val sms_marketing_consent: SmsMarketingConsent? = null,
    val admin_graphql_api_id: String? = null,
    val default_address: CustomerAddress? = null
)

data class CustomerAddress(
    val id: Long? = null,
    val customer_id: Long?= null,
    val first_name: String?= null,
    val last_name: String?= null,
    val company: String?= null,
    val address1: String?= null,
    val address2: String?= null,
    val city: String?= null,
    val province: String?= null,
    val country: String?= null,
    val zip: String?= null,
    val phone: String?= null,
    val name: String?= null,
    val province_code: String?= null,
    val country_code: String?= null,
    val country_name: String?= null,
    val default: Boolean? = false
)

data class EmailMarketingConsent(
    val state: String,
    val opt_in_level: String,
    val consent_updated_at: String?
)

data class SmsMarketingConsent(
    val state: String,
    val opt_in_level: String,
    val consent_updated_at: String?,
    val consent_collected_from: String
)
//data class CustomerId(id:Long)