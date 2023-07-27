package com.example.market.data.pojo

data class DiscountResponse(
    val price_rules: List<PriceRule>? = null
)

data class PriceRule(
    val admin_graphql_api_id: String? = null,
    val allocation_limit: Any? = null,
    val allocation_method: String? = null,
    val created_at: String? = null,
    val customer_segment_prerequisite_ids: List<Any>? = null,
    val customer_selection: String? = null,
    val ends_at: Any? = null,
    val entitled_collection_ids: List<Any>? = null,
    val entitled_country_ids: List<Any>? = null,
    val entitled_product_ids: List<Any>? = null,
    val entitled_variant_ids: List<Any>? = null,
    val id: Long? = null,
    val once_per_customer: Boolean? = null,
    val prerequisite_collection_ids: List<Any>? = null,
    val prerequisite_customer_ids: List<Any>? = null,
    val prerequisite_product_ids: List<Any>? = null,
    val prerequisite_quantity_range: PrerequisiteQuantityRange? = null,
    val prerequisite_shipping_price_range: Any? = null,
    val prerequisite_subtotal_range: PrerequisiteSubtotalRange? = null,
    val prerequisite_to_entitlement_purchase: PrerequisiteToEntitlementPurchase? = null,
    val prerequisite_to_entitlement_quantity_ratio: PrerequisiteToEntitlementQuantityRatio? = null,
    val prerequisite_variant_ids: List<Any>? = null,
    val starts_at: String? = null,
    val target_selection: String? = null,
    val target_type: String? = null,
    val title: String? = null,
    val updated_at: String? = null,
    val usage_limit: Any? = null,
    val value: String? = null,
    val value_type: String? = null
)
data class PrerequisiteQuantityRange(
    val greater_than_or_equal_to: Int? = null
)
data class PrerequisiteSubtotalRange(
    val greater_than_or_equal_to: String? = null
)
data class PrerequisiteToEntitlementPurchase(
    val prerequisite_amount: Any? = null
)
data class PrerequisiteToEntitlementQuantityRatio(
    val entitled_quantity: Any? = null,
    val prerequisite_quantity: Any? = null
)