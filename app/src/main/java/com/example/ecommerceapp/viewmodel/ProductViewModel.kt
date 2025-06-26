package com.example.ecommerceapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.repository.ProductRepository
import com.example.ecommerceapp.domain.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProductRepository(application.applicationContext)

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val list = repository.loadProducts()
                _products.value = list
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur lors du chargement des produits"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Chargement automatique au démarrage (optionnel)
    init {
        loadProducts()
    }
}
