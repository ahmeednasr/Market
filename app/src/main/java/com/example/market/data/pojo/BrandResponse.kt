package com.example.market.data.pojo

data class BrandResponse(
    val smart_collections: List<SmartCollection>?
)

data class SmartCollection(
    val admin_graphql_api_id: String?,
    val body_html: String?,
    val disjunctive: Boolean?,
    val handle: String?,
    val id: Long?,
    val image: BrandImage?,
    val published_at: String?,
    val published_scope: String?,
    val rules: List<Rule>?,
    val sort_order: String?,
    val template_suffix: Any?,
    val title: String?,
    val updated_at: String?
)

data class Rule(
    val column: String?,
    val condition: String?,
    val relation: String?
)

data class BrandImage(
    val alt: Any?,
    val created_at: String?,
    val height: Int?,
    val src: String?,
    val width: Int?
)