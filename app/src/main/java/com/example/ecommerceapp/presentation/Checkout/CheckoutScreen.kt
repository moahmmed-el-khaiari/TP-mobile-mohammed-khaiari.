package com.example.ecommerceapp.presentation.checkout

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ecommerceapp.data.ClientData
import com.example.ecommerceapp.data.local.AppDatabase
import com.example.ecommerceapp.data.local.entity.CartItem
import com.example.ecommerceapp.data.repository.OrderRepository
import com.example.ecommerceapp.viewmodel.CartViewModel
import com.example.ecommerceapp.viewmodel.OrderViewModel
import com.example.ecommerceapp.viewmodel.OrderViewModelFactory

@Composable
fun CheckoutScreen(
    navController: NavController,
    viewModel: CartViewModel = viewModel()
) {
    // ✅ Injection manuelle du OrderViewModel avec Factory
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val db = AppDatabase.getDatabase(application)
    val orderRepository = remember { OrderRepository(db.orderDao(), db.orderItemDao()) }

    val orderViewModel: OrderViewModel = viewModel(
        factory = OrderViewModelFactory(orderRepository)
    )

    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val cartItems by viewModel.cartItems.collectAsState(initial = emptyList())

    fun validateInputs(): String? {
        return when {
            name.isBlank() -> "Veuillez saisir votre nom complet"
            address.isBlank() -> "Veuillez saisir votre adresse"
            phone.isBlank() -> "Veuillez saisir votre téléphone"
            cardNumber.length != 16 -> "Le numéro de carte doit contenir 16 chiffres"
            cvv.length !in 3..4 -> "Le CVV doit contenir 3 ou 4 chiffres"
            email.isBlank() -> "Veuillez saisir votre email"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email invalide"
            else -> null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Détails de votre commande") })
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nom complet") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Adresse complète") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Téléphone") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = cardNumber, onValueChange = { cardNumber = it }, label = { Text("Numéro de carte") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = cvv, onValueChange = { cvv = it }, label = { Text("CVV") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword), singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), singleLine = true, modifier = Modifier.fillMaxWidth())

                errorMessage?.let {
                    Text(text = it, color = MaterialTheme.colors.error)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val validationError = validateInputs()
                        if (validationError != null) {
                            errorMessage = validationError
                        } else {
                            errorMessage = null

                            val clientData = ClientData(name, address, phone, email)

                            orderViewModel.placeOrder(
                                cartItems = cartItems,
                                clientData = clientData,
                                onSuccess = {
                                    viewModel.generateReceipt(clientData, cartItems)
                                    viewModel.clearCart()
                                    navController.navigate("orderConfirmation") {
                                        popUpTo("cart") { inclusive = true }
                                    }
                                },
                                onError = { msg ->
                                    errorMessage = msg
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Valider la commande")
                }
            }
        }
    )
}
