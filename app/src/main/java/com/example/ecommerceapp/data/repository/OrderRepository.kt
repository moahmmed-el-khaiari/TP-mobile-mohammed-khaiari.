package com.example.ecommerceapp.data.repository

import com.example.ecommerceapp.data.local.dao.OrderDao
import com.example.ecommerceapp.data.local.dao.OrderItemDao
import com.example.ecommerceapp.data.local.entity.Order
import com.example.ecommerceapp.data.local.entity.OrderItem
import com.example.ecommerceapp.data.mapper.mapToDomain
import com.example.ecommerceapp.domain.OrderDomain

class OrderRepository(
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao
) {

    suspend fun insertOrder(order: Order): Long {
        return orderDao.insertOrder(order)
    }

    suspend fun insertOrderItems(items: List<OrderItem>) {
        orderItemDao.insertOrderItems(items)
    }

    suspend fun getAllOrders(): List<OrderDomain> {
        val orders = orderDao.getAllOrders()
        return orders.map { order ->
            val items = orderItemDao.getItemsForOrder(order.id)
            mapToDomain(order, items)
        }
    }

    suspend fun getOrderById(orderId: Int): OrderDomain? {
        val order = orderDao.getOrderById(orderId) ?: return null
        val items = orderItemDao.getItemsForOrder(orderId)
        return mapToDomain(order, items)
    }
}
