package com.example.ecommerceapp.presentation.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ecommerceapp.data.local.entity.CartItem
import com.example.ecommerceapp.viewmodel.CartViewModel

@Composable
fun CartScreen(
    navController: NavController, // pour la navigation vers confirmation
    viewModel: CartViewModel = viewModel()
) {
    val cartItems by viewModel.cartItems.collectAsState()

    val total = cartItems.sumOf { it.price * it.quantity }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mon Panier") },
                actions = {
                    if (cartItems.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearCart() }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Vider le panier"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                BottomAppBar {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total : ${"%.2f".format(total)} MAD", style = MaterialTheme.typography.h6)
                        Button(onClick = {
                            navController.navigate("checkout")
                        }) {
                            Text("Commander")
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("🛒 Votre panier est vide", style = MaterialTheme.typography.h6)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                items(cartItems) { item ->
                    CartItemRow(item, viewModel)
                }
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItem, viewModel: CartViewModel) {
    val context = LocalContext.current
    val imageResId = context.resources.getIdentifier(
        item.imageName,
        "drawable",
        context.packageName
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 4.dp
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            if (imageResId != 0) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = item.name,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 16.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = item.name, style = MaterialTheme.typography.h6)
                Text(text = "${item.price} MAD", style = MaterialTheme.typography.body2)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Button(onClick = {
                        if (item.quantity > 1) {
                            viewModel.updateQuantity(item.id, item.quantity - 1)
                        } else {
                            viewModel.removeFromCart(item)
                        }
                    }) {
                        Text("-")
                    }

                    Text(
                        text = item.quantity.toString(),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Button(onClick = {
                        viewModel.updateQuantity(item.id, item.quantity + 1)
                    }) {
                        Text("+")
                    }
                }
            }
        }
    }
}
