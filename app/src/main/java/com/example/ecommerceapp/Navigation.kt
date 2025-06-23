package com.example.ecommerceapp

import OrderConfirmationScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.ecommerceapp.presentation.cart.CartScreen
import com.example.ecommerceapp.presentation.checkout.CheckoutScreen
import com.example.ecommerceapp.presentation.home.HomeScreen

import com.example.ecommerceapp.presentation.product.ProductDetailScreen

@Composable
fun EcommerceAppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            HomeScreen(navController)
        }
        composable("cart") {
            CartScreen(navController)
        }
        composable("orderConfirmation") {
            OrderConfirmationScreen(navController)
        }
        composable("details/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
            if (productId != null) {
                ProductDetailScreen(productId = productId, navController = navController)
            }
        }
        composable("checkout") {
            CheckoutScreen(
                navController = navController,
                onOrderConfirmed = {
                    navController.navigate("orderConfirmation") {
                        popUpTo("cart") { inclusive = true }
                    }
                }
            )

        }
    }
}


