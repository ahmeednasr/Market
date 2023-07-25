package com.example.market.data.pojo

import java.io.Serializable

data class ProductResponse(
    val products: List<Product>? = null,
    val product: Product? = null
)

data class Product(
    val admin_graphql_api_id: String? = null,
    val body_html: String? = null,
    val created_at: String? = null,
    val handle: String? = null,
    val id: Long? = null,
    val image: ProductImage? = null,
    val images: List<ProductImage>? = null,
    val options: List<Option>? = null,
    val product_type: String? = null,
    val published_at: String? = null,
    val published_scope: String? = null,
    val status: String? = null,
    val tags: String? = null,
    val template_suffix: Any? = null,
    val title: String? = null,
    val updated_at: String? = null,
    val variants: List<Variant>? = null,
    val vendor: String? = null,
    var isFavourite: Boolean = false,
) : Serializable

data class Variant(
    val admin_graphql_api_id: String? = null,
    val barcode: Any? = null,
    val compare_at_price: String? = null,
    val created_at: String? = null,
    val fulfillment_service: String? = null,
    val grams: Int? = null,
    val id: Long? = null,
    val image_id: Any? = null,
    val inventory_item_id: Long? = null,
    val inventory_management: String? = null,
    val inventory_policy: String? = null,
    val inventory_quantity: Int? = null,
    val old_inventory_quantity: Int? = null,
    val option1: String? = null,
    val option2: String? = null,
    val option3: Any? = null,
    val position: Int? = null,
    val price: String? = null,
    val product_id: Long? = null,
    val requires_shipping: Boolean? = null,
    val sku: String? = null,
    val taxable: Boolean? = null,
    val title: String? = null,
    val updated_at: String? = null,
    val weight: Int? = null,
    val weight_unit: String? = null
)

data class Option(
    val id: Long,
    val name: String,
    val position: Int,
    val product_id: Long,
    val values: List<String>
)

data class ProductImage(
    val admin_graphql_api_id: String? = null,
    val alt: Any? = null,
    val created_at: String? = null,
    val height: Int? = null,
    val id: Long? = null,
    val position: Int? = null,
    val product_id: Long? = null,
    val src: String? = null,
    val updated_at: String? = null,
    val variant_ids: List<Any>? = null,
    val width: Int? = null
)