package com.example.ecommerceapp.domain
data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageName: String
)
