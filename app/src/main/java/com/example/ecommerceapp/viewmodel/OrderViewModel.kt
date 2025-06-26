package com.example.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.ClientData
import com.example.ecommerceapp.data.local.entity.CartItem
import com.example.ecommerceapp.data.local.entity.Order
import com.example.ecommerceapp.data.local.entity.OrderItem
import com.example.ecommerceapp.data.repository.OrderRepository
import com.example.ecommerceapp.domain.OrderDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrderViewModel(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _orders = MutableStateFlow<List<OrderDomain>>(emptyList())
    val orders: StateFlow<List<OrderDomain>> = _orders

    private val _recentOrders = MutableStateFlow<List<OrderDomain>>(emptyList())
    val recentOrders: StateFlow<List<OrderDomain>> = _recentOrders

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val orderList = orderRepository.getAllOrders()
                _orders.value = orderList

                _recentOrders.value = orderList
                    .sortedByDescending { it.timestamp }
                    .take(5)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Erreur inconnue"
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun getOrderById(orderId: Int): OrderDomain? {
        return try {
            orderRepository.getOrderById(orderId)
        } catch (e: Exception) {
            null
        }
    }

    fun refreshOrders() {
        loadOrders()
    }

    // ✅ NOUVELLE MÉTHODE : enregistrement d'une commande complète
    fun placeOrder(cartItems: List<CartItem>, clientData: ClientData, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // 1. Créer l'objet Order
                val order = Order(
                    items = cartItems.map { it.productId }.toString(),
                    totalAmount = cartItems.sumOf { it.price * it.quantity }
                )

                // 2. Insérer la commande et récupérer son ID
                val orderId = orderRepository.insertOrder(order)

                // 3. Créer et insérer les OrderItems
                val orderItems = cartItems.map {
                    OrderItem(
                        orderId = orderId.toInt(),
                        productId = it.productId,
                        name = it.name,
                        quantity = it.quantity,
                        price = it.price,
                        imageUrl = it.imageName
                    )
                }
                orderRepository.insertOrderItems(orderItems)

                // 4. Recharger les commandes
                loadOrders()

                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Échec de la commande")
            }
        }
    }
}
