package com.example.market.data.pojo

data class BrandResponse(
    val smart_collections: List<SmartCollection>?
)

data class SmartCollection(
    val admin_graphql_api_id: String? = null,
    val body_html: String? = null,
    val disjunctive: Boolean? = null,
    val handle: String? = null,
    val id: Long? = null,
    val image: BrandImage? = null,
    val published_at: String? = null,
    val published_scope: String? = null,
    val rules: List<Rule>? = null,
    val sort_order: String? = null,
    val template_suffix: Any? = null,
    val title: String? = null,
    val updated_at: String?  = null
)

data class Rule(
    val column: String? = null,
    val condition: String? = null,
    val relation: String? = null
)

data class BrandImage(
    val alt: Any? = null,
    val created_at: String? = null,
    val height: Int? = null,
    val src: String? = null,
    val width: Int? = null
)