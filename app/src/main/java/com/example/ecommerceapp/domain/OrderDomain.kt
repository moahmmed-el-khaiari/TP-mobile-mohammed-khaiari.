package com.example.ecommerceapp.domain

import com.example.ecommerceapp.data.local.entity.OrderItem

data class OrderDomain(
    val id: Int,
    val items: List<OrderItem>, // Tu peux créer un OrderItemDomain si tu veux découpler
    val total: Double,
    val status: OrderStatus,
    val timestamp: Long
)

enum class OrderStatus {
    EN_ATTENTE, EN_PREPARATION, EXPEDIE, LIVRE, ANNULE
}
