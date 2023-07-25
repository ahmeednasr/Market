package com.example.market.data.pojo

data class DiscountResponse(
    val price_rules: List<PriceRule>
)

data class PriceRule(
    val admin_graphql_api_id: String,
    val allocation_limit: Any,
    val allocation_method: String,
    val created_at: String,
    val customer_segment_prerequisite_ids: List<Any>,
    val customer_selection: String,
    val ends_at: Any,
    val entitled_collection_ids: List<Any>,
    val entitled_country_ids: List<Any>,
    val entitled_product_ids: List<Any>,
    val entitled_variant_ids: List<Any>,
    val id: Long,
    val once_per_customer: Boolean,
    val prerequisite_collection_ids: List<Any>,
    val prerequisite_customer_ids: List<Any>,
    val prerequisite_product_ids: List<Any>,
    val prerequisite_quantity_range: PrerequisiteQuantityRange,
    val prerequisite_shipping_price_range: Any,
    val prerequisite_subtotal_range: PrerequisiteSubtotalRange,
    val prerequisite_to_entitlement_purchase: PrerequisiteToEntitlementPurchase,
    val prerequisite_to_entitlement_quantity_ratio: PrerequisiteToEntitlementQuantityRatio,
    val prerequisite_variant_ids: List<Any>,
    val starts_at: String,
    val target_selection: String,
    val target_type: String,
    val title: String,
    val updated_at: String,
    val usage_limit: Any,
    val value: String,
    val value_type: String
)
data class PrerequisiteQuantityRange(
    val greater_than_or_equal_to: Int
)
data class PrerequisiteSubtotalRange(
    val greater_than_or_equal_to: String
)
data class PrerequisiteToEntitlementPurchase(
    val prerequisite_amount: Any
)
data class PrerequisiteToEntitlementQuantityRatio(
    val entitled_quantity: Any,
    val prerequisite_quantity: Any
)