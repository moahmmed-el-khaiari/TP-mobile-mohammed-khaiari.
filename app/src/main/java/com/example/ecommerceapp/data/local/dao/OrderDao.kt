package com.example.ecommerceapp.data.local.dao

import androidx.room.*
import com.example.ecommerceapp.data.local.entity.Order

@Dao
interface OrderDao {

    @Insert
    suspend fun insertOrder(order: Order): Long

    @Query("SELECT * FROM orders ORDER BY date DESC")
    suspend fun getAllOrders(): List<Order>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: Int): Order?

    @Update
    suspend fun updateOrder(order: Order)
}
