package com.example.market.data.pojo

import com.google.gson.annotations.SerializedName

data class CustomerResponse(
    val customer: Customer
)
data class CustomersResponse(
    val customers: List<Customer>
)
data class NewUser(
    val customer:User
)
data class User(
    val first_name: String,
    val last_name: String,
    val email: String,
    val phone: String,
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
)

data class Customer(
    val id: Long,
    val email: String,
    val accepts_marketing: Boolean,
    val created_at: String,
    val updated_at: String,
    val first_name: String,
    val last_name: String,
    val orders_count: Int,
    val state: String,
    val total_spent: String,
    val last_order_id: Long?,
    val note: String?,
    val verified_email: Boolean,
    val multipass_identifier: String?,
    val tax_exempt: Boolean,
    val tags: String,
    val last_order_name: String?,
    val currency: String,
    val phone: String,
    val addresses: List<CustomerAddress>,
    val accepts_marketing_updated_at: String,
    val marketing_opt_in_level: String?,
    val tax_exemptions: List<String>,
    val email_marketing_consent: EmailMarketingConsent?,
    val sms_marketing_consent: SmsMarketingConsent?,
    val admin_graphql_api_id: String,
    val default_address: CustomerAddress
)

data class CustomerAddress(
    val id: Long,
    val customer_id: Long,
    val first_name: String,
    val last_name: String,
    val company: String?,
    val address1: String,
    val address2: String?,
    val city: String,
    val province: String,
    val country: String,
    val zip: String,
    val phone: String,
    val name: String,
    val province_code: String,
    val country_code: String,
    val country_name: String,
    val default: Boolean
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