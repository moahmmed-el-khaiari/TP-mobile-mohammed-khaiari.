package com.example.ecommerceapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.ClientData
import com.example.ecommerceapp.data.local.AppDatabase
import com.example.ecommerceapp.data.local.entity.CartItem
import com.example.ecommerceapp.data.local.entity.Order
import com.example.ecommerceapp.data.local.entity.OrderItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val cartDao = db.cartDao()


    private val _receipt = MutableStateFlow<String?>(null)
    val receipt: StateFlow<String?> = _receipt

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    init {
        loadCartItems()
    }

    fun loadCartItems() {
        viewModelScope.launch {
            cartDao.getAllItems().collect { items ->
                _cartItems.value = items
            }
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            cartDao.delete(cartItem)
            loadCartItems()
        }
    }
    fun addToCart(item: CartItem) = viewModelScope.launch {
        cartDao.insert(item)
    }


    fun clearCart() {
        viewModelScope.launch {
            cartDao.clearCart()
            loadCartItems()
        }
    }

    fun updateQuantity(id: Int, quantity: Int) {
        viewModelScope.launch {
            cartDao.updateQuantity(id, quantity)
            loadCartItems()
        }
    }

    fun getTotalPrice(): Double {
        return _cartItems.value.sumOf { it.price * it.quantity }
    }

    fun generateReceipt(clientData: ClientData, items: List<CartItem>): String {
        val receipt = StringBuilder()
        receipt.append("Client: ${clientData.name}\n")
        receipt.append("Adresse: ${clientData.address}\n")
        receipt.append("Téléphone: ${clientData.phone}\n")
        receipt.append("Email: ${clientData.email}\n\n")
        receipt.append("Articles commandés:\n")

        items.forEach {
            receipt.append("- ${it.name} x${it.quantity} = ${it.price * it.quantity} MAD\n")
        }

        val total = items.sumOf { it.price * it.quantity }
        receipt.append("\nTotal: $total MAD")

        return receipt.toString()
    }

    fun placeOrder(cartItems: List<CartItem>) {
        viewModelScope.launch {
            val orderDao = db.orderDao()
            val orderItemDao = db.orderItemDao()

            val total = cartItems.sumOf { it.price * it.quantity }

            val orderId = orderDao.insertOrder(
                Order(
                    items = cartItems.joinToString(",") { it.productId.toString() },
                    totalAmount = total,
                    status = "EN_ATTENTE"
                )
            )

            val items = cartItems.map {
                OrderItem(
                    orderId = orderId.toInt(),
                    productId = it.productId,
                    name = it.name,
                    quantity = it.quantity,
                    price = it.price,
                    imageUrl = it.imageName
                )
            }

            orderItemDao.insertOrderItems(items)
        }
    }
}
