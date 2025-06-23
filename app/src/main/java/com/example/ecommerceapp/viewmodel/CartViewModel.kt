package com.example.ecommerceapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.ClientData
import com.example.ecommerceapp.data.local.AppDatabase
import com.example.ecommerceapp.data.local.entity.CartItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).cartDao()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _receipt = MutableStateFlow<String?>(null)
    val receipt: StateFlow<String?> = _receipt

    init {
        viewModelScope.launch {
            dao.getAllItems().collect { items ->
                _cartItems.value = items
            }
        }
    }

    fun addToCart(item: CartItem) = viewModelScope.launch {
        dao.insert(item)
    }

    fun removeFromCart(item: CartItem) = viewModelScope.launch {
        dao.delete(item)
    }

    fun updateQuantity(id: Int, quantity: Int) = viewModelScope.launch {
        dao.updateQuantity(id, quantity)
    }

    fun clearCart() = viewModelScope.launch {
        dao.clearCart()
    }

    fun generateReceipt(clientData: ClientData, cartItems: List<CartItem>) {
        val receiptText = buildString {
            appendLine("----- Reçu de commande -----")
            appendLine("Client: ${clientData.name}")
            appendLine("Adresse: ${clientData.address}")
            appendLine("Téléphone: ${clientData.phone}")
            appendLine("Email: ${clientData.email}")
            appendLine("")
            appendLine("Articles commandés:")
            cartItems.forEach { item ->
                appendLine("- ${item.name} x${item.quantity} : ${item.price * item.quantity} MAD")
            }
            val total = cartItems.sumOf { it.price * it.quantity }
            appendLine("")
            appendLine("Total à payer: $total MAD")
            appendLine("----------------------------")
        }
        _receipt.value = receiptText
    }
}
