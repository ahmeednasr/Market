package com.example.market.data.pojo

import java.io.Serializable

data class ProductResponse(
    val products: List<Product>?
)

data class Product(
    val admin_graphql_api_id: String?,
    val body_html: String?,
    val created_at: String?,
    val handle: String?,
    val id: Long?,
    val image: ProductImage?,
    val images: List<ProductImage>?,
    val options: List<Option>?,
    val product_type: String?,
    val published_at: String?,
    val published_scope: String?,
    val status: String?,
    val tags: String?,
    val template_suffix: Any?,
    val title: String?,
    val updated_at: String?,
    val variants: List<Variant>?,
    val vendor: String?,
    var isFavourite: Boolean = false
) : Serializable

data class Variant(
    val admin_graphql_api_id: String?,
    val barcode: Any?,
    val compare_at_price: String?,
    val created_at: String?,
    val fulfillment_service: String?,
    val grams: Int?,
    val id: Long?,
    val image_id: Any?,
    val inventory_item_id: Long?,
    val inventory_management: String?,
    val inventory_policy: String?,
    val inventory_quantity: Int?,
    val old_inventory_quantity: Int?,
    val option1: String?,
    val option2: String?,
    val option3: Any?,
    val position: Int?,
    val price: String?,
    val product_id: Long?,
    val requires_shipping: Boolean?,
    val sku: String?,
    val taxable: Boolean?,
    val title: String?,
    val updated_at: String?,
    val weight: Int?,
    val weight_unit: String?
)

data class Option(
    val id: Long,
    val name: String,
    val position: Int,
    val product_id: Long,
    val values: List<String>
)

data class ProductImage(
    val admin_graphql_api_id: String?,
    val alt: Any?,
    val created_at: String?,
    val height: Int?,
    val id: Long?,
    val position: Int?,
    val product_id: Long?,
    val src: String?,
    val updated_at: String?,
    val variant_ids: List<Any>?,
    val width: Int?
)