package com.example.ecommerceapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_items")
data class OrderItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderId: Int, // lien vers OrderEntity.id
    val productId: Int,
    val name: String,
    val quantity: Int,
    val price: Double,
    val imageUrl: String
)
