package com.example.market.data.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DraftOrderResponse(
    @field:SerializedName("draft_order")
    @Expose val draftOrder: DraftOrder? = null,
)

data class DraftOrder(
    @field:SerializedName("note")
    @Expose val note: String? = null,

    @field:SerializedName("applied_discount")
    @Expose val appliedDiscount: AppliedDiscount? = null,

    @field:SerializedName("created_at")
    @Expose val createdAt: String? = null,

    @field:SerializedName("billing_address")
    @Expose val billingAddress: BillingAddress? = null,

    @field:SerializedName("taxes_included")
    @Expose val taxesIncluded: Boolean? = null,

    @field:SerializedName("line_items")
    @Expose val lineItems: List<LineItemsItem?>? = null,

    @field:SerializedName("payment_terms")
    @Expose val paymentTerms: Any? = null,

    @field:SerializedName("updated_at")
    @Expose val updatedAt: String? = null,

    @field:SerializedName("tax_lines")
    @Expose val taxLines: List<TaxLinesItem?>? = null,

    @field:SerializedName("currency")
    @Expose val currency: String? = null,

    @field:SerializedName("id")
    @Expose val id: Long? = null,

    @field:SerializedName("shipping_address")
    @Expose val shippingAddress: ShippingAddress? = null,

    @field:SerializedName("email")
    @Expose var email: String? = null,

    @field:SerializedName("subtotal_price")
    @Expose val subtotalPrice: String? = null,

    @field:SerializedName("total_price")
    @Expose val totalPrice: String? = null,

    @field:SerializedName("tax_exempt")
    @Expose val taxExempt: Boolean? = null,

    @field:SerializedName("invoice_sent_at")
    @Expose val invoiceSentAt: Any? = null,

    @field:SerializedName("total_tax")
    @Expose val totalTax: String? = null,

    @field:SerializedName("tags")
    @Expose val tags: String? = null,

    @field:SerializedName("completed_at")
    @Expose val completedAt: Any? = null,

    @field:SerializedName("note_attributes")
    @Expose val noteAttributes: List<Any?>? = null,

    @field:SerializedName("admin_graphql_api_id")
    @Expose val adminGraphqlApiId: String? = null,

    @field:SerializedName("name")
    @Expose val name: String? = null,

    @field:SerializedName("shipping_line")
    @Expose val shippingLine: ShippingLine? = null,

    @field:SerializedName("order_id")
    @Expose val orderId: Any? = null,

    @field:SerializedName("invoice_url")
    @Expose val invoiceUrl: String? = null,

    @field:SerializedName("status")
    @Expose val status: String? = null,

    @field:SerializedName("customer")
    @Expose val customer: Customer? = null
)

data class AppliedDiscount(

    @field:SerializedName("amount")
    @Expose val amount: String? = null,

    @field:SerializedName("value_type")
    @Expose val valueType: String? = null,

    @field:SerializedName("description")
    @Expose val description: String? = null,

    @field:SerializedName("title")
    @Expose val title: String? = null,

    @field:SerializedName("value")
    @Expose val value: String? = null
)

data class BillingAddress(

    @field:SerializedName("zip")
    @Expose val zip: String? = null,

    @field:SerializedName("country")
    @Expose val country: String? = null,

    @field:SerializedName("city")
    @Expose val city: String? = null,

    @field:SerializedName("address2")
    @Expose val address2: String? = null,

    @field:SerializedName("address1")
    @Expose val address1: String? = null,

    @field:SerializedName("latitude")
    @Expose val latitude: Double? = null,

    @field:SerializedName("last_name")
    @Expose val lastName: String? = null,

    @field:SerializedName("province_code")
    @Expose val provinceCode: String? = null,

    @field:SerializedName("country_code")
    @Expose val countryCode: String? = null,

    @field:SerializedName("province")
    @Expose val province: String? = null,

    @field:SerializedName("phone")
    @Expose val phone: String? = null,

    @field:SerializedName("name")
    @Expose val name: String? = null,

    @field:SerializedName("company")
    @Expose val company: Any? = null,

    @field:SerializedName("first_name")
    @Expose val firstName: String? = null,

    @field:SerializedName("longitude")
    @Expose val longitude: Double? = null
)

data class LineItemsItem(

    @SerializedName("properties")
    var properties: List<Property>? = null,

    @field:SerializedName("vendor")
    @Expose val vendor: String? = null,

    @field:SerializedName("quantity")
    var quantity: Int? = null,

    @field:SerializedName("taxable")
    @Expose val taxable: Boolean? = null,

    @field:SerializedName("gift_card")
    @Expose val giftCard: Boolean? = null,

    @field:SerializedName("fulfillment_service")
    @Expose val fulfillmentService: String? = null,

    @field:SerializedName("applied_discount")
    @Expose val appliedDiscount: AppliedDiscount? = null,

    @field:SerializedName("requires_shipping")
    @Expose val requiresShipping: Boolean? = null,

    @field:SerializedName("custom")
    @Expose val custom: Boolean? = null,

    @field:SerializedName("title")
    @Expose val title: String? = null,

    @field:SerializedName("variant_id")
    @Expose val variantId: Long? = null,

    @field:SerializedName("price")
    @Expose val price: String? = null,

    @field:SerializedName("product_id")
    @Expose val productId: Long? = null,

    @field:SerializedName("admin_graphql_api_id")
    @Expose val adminGraphqlApiId: String? = null,

    @field:SerializedName("name")
    @Expose val name: String? = null,

    @field:SerializedName("id")
    @Expose val id: Long? = null,

    @field:SerializedName("sku")
    @Expose val sku: String? = null,

    @field:SerializedName("grams")
    @Expose val grams: Int? = null,

    @field:SerializedName("variant_title")
    @Expose val variantTitle: String? = null,
)

data class TaxLinesItem(

    @field:SerializedName("rate")
    @Expose val rate: Double? = null,

    @field:SerializedName("price")
    @Expose val price: String? = null,

    @field:SerializedName("title")
    @Expose val title: String? = null
)

data class ShippingAddress(

    @field:SerializedName("zip")
    @Expose val zip: String? = null,

    @field:SerializedName("country")
    @Expose val country: String? = null,

    @field:SerializedName("city")
    @Expose val city: String? = null,

    @field:SerializedName("address2")
    @Expose val address2: String? = null,

    @field:SerializedName("address1")
    @Expose val address1: String? = null,

    @field:SerializedName("latitude")
    @Expose val latitude: Double? = null,

    @field:SerializedName("last_name")
    @Expose val lastName: String? = null,

    @field:SerializedName("province_code")
    @Expose val provinceCode: String? = null,

    @field:SerializedName("country_code")
    @Expose val countryCode: String? = null,

    @field:SerializedName("province")
    @Expose val province: String? = null,

    @field:SerializedName("phone")
    @Expose val phone: String? = null,

    @field:SerializedName("name")
    @Expose val name: String? = null,

    @field:SerializedName("company")
    @Expose val company: Any? = null,

    @field:SerializedName("first_name")
    @Expose val firstName: String? = null,

    @field:SerializedName("longitude")
    @Expose val longitude: Double? = null
)

data class ShippingLine(

    @field:SerializedName("price")
    @Expose val price: String? = null,

    @field:SerializedName("custom")
    @Expose val custom: Boolean? = null,

    @field:SerializedName("handle")
    @Expose val handle: Any? = null,

    @field:SerializedName("title")
    @Expose val title: String? = null
)

data class Property(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("value")
    val value: String? = null
)