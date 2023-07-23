package com.example.market.data.pojo

data class CartResponse(
    val draft_orders: List<CartDraftOrder>
)

data class CartDraftOrder(
    val admin_graphql_api_id: String,
    val applied_discount: AppliedDiscount,
    val billing_address: Any,
    val completed_at: Any,
    val created_at: String,
    val currency: String,
    val customer: CustomerCart?,
    val email: String,
    val id: Long,
    val invoice_sent_at: Any,
    val invoice_url: String,
    val line_items: List<LineItemCart>?,
    val name: String,
    val note: Any,
    val note_attributes: List<Any>,
    val order_id: Any,
    val payment_terms: Any,
    val shipping_address: Any,
    val shipping_line: Any,
    val status: String,
    val subtotal_price: String,
    val tags: String,
    val tax_exempt: Boolean,
    val tax_lines: List<TaxLineCart>,
    val taxes_included: Boolean,
    val total_price: String,
    val total_tax: String,
    val updated_at: String
)

data class CustomerCart(
    val accepts_marketing: Boolean?,
    val accepts_marketing_updated_at: String?,
    val admin_graphql_api_id: String?,
    val created_at: String?,
    val currency: String?,
    val email: String?,
    val email_marketing_consent: EmailMarketingConsent?,
    val first_name: String?,
    val id: Long?,
    val last_name: String?,
    val last_order_id: Any?,
    val last_order_name: Any?,
    val marketing_opt_in_level: Any?,
    val multipass_identifier: String?,
    val note: String?,
    val orders_count: Int?,
    val phone: String?,
    val sms_marketing_consent: SmsMarketingConsent?,
    val state: String?,
    val tags: String?,
    val tax_exempt: Boolean?,
    val tax_exemptions: List<Any>?,
    val total_spent: String?,
    val updated_at: String?,
    val verified_email: Boolean?
)

data class LineItemCart(
    val admin_graphql_api_id: String,
    val applied_discount: Any,
    val custom: Boolean,
    val fulfillment_service: String,
    val gift_card: Boolean,
    val grams: Int,
    val id: Long,
    val name: String,
    val price: String,
    val product_id: Long,
    val properties: List<Property>,
    val quantity: Int,
    val requires_shipping: Boolean,
    val sku: String,
    val tax_lines: List<TaxLineCart>,
    val taxable: Boolean,
    val title: String,
    val variant_id: Long,
    val variant_title: String,
    val vendor: String
)

data class TaxLineCart(
    val price: String,
    val rate: Double,
    val title: String
)