package com.example.ecommerceapp

import OrderConfirmationScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ecommerceapp.data.local.SessionManager
import com.example.ecommerceapp.data.local.SessionManager.isLoggedIn
import com.example.ecommerceapp.presentation.auth.LoginScreen
import com.example.ecommerceapp.presentation.auth.RegisterScreen
import com.example.ecommerceapp.presentation.cart.CartScreen
import com.example.ecommerceapp.presentation.checkout.CheckoutScreen
import com.example.ecommerceapp.presentation.home.HomeScreen
import com.example.ecommerceapp.presentation.order.OrderDetailScreen
import com.example.ecommerceapp.presentation.order.OrderHistoryScreen
import com.example.ecommerceapp.presentation.product.ProductDetailScreen
import com.example.ecommerceapp.presentation.profile.ProfileScreen
import com.example.ecommerceapp.viewmodel.UserViewModel

@Composable
fun EcommerceAppNavigation(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val navController = rememberNavController()

    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        SessionManager.isLoggedIn(context).collect { loggedIn ->
            startDestination = if (loggedIn) "home" else "login"
        }
    }

    if (startDestination == null) {
        // Affiche un écran de chargement temporaire
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = startDestination!!,
            modifier = modifier
        ) {
            composable("login") {
                LoginScreen(navController = navController)
            }
            composable("register") {
                RegisterScreen(navController = navController)
            }
            composable("home") {
                HomeScreen(navController = navController)
            }
            composable("cart") {
                CartScreen(navController = navController)
            }
            composable("checkout") {
                CheckoutScreen(navController = navController)
            }
            composable("orderHistory") {
                OrderHistoryScreen(navController = navController)
            }
            composable("orderConfirmation") {
                OrderConfirmationScreen(navController = navController)
            }
            composable("orderDetail/{orderId}") { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId")?.toIntOrNull()
                if (orderId != null) {
                    OrderDetailScreen(orderId = orderId)
                }
            }
            composable("profile") {
                val userViewModel: UserViewModel = viewModel()
                ProfileScreen(
                    navController = navController,
                    userViewModel = userViewModel
                )
            }
            composable("details/{productId}") { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
                if (productId != null) {
                    ProductDetailScreen(productId = productId, navController = navController)
                }
            }
        }
    }
}
