package com.example.ecommerceapp.data.mapper

import com.example.ecommerceapp.data.local.entity.Order
import com.example.ecommerceapp.data.local.entity.OrderItem
import com.example.ecommerceapp.domain.OrderDomain
import com.example.ecommerceapp.domain.OrderStatus

fun mapToDomain(order: Order, items: List<OrderItem>): OrderDomain {
    return OrderDomain(
        id = order.id,
        items = items,
        total = order.totalAmount,
        status = OrderStatus.valueOf(order.status),
        timestamp = order.date
    )
}
