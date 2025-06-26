package com.example.ecommerceapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.ecommerceapp.data.local.entity.OrderItem

@Dao
interface OrderItemDao {

    @Insert
    suspend fun insertOrderItem(orderItem: OrderItem)

    @Insert
    suspend fun insertOrderItems(orderItems: List<OrderItem>)

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getItemsForOrder(orderId: Int): List<OrderItem>
}
