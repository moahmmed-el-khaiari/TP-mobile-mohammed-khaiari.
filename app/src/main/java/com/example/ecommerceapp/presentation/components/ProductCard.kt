package com.example.ecommerceapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ecommerceapp.domain.Product
import com.example.ecommerceapp.data.local.entity.CartItem
import com.example.ecommerceapp.viewmodel.CartViewModel
import kotlinx.coroutines.launch

@Composable
fun ProductCard(
    product: Product,
    navController: NavController,
    cartViewModel: CartViewModel
) {
    val context = LocalContext.current
    val imageResId = context.resources.getIdentifier(
        product.imageName, "drawable", context.packageName
    )

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (imageResId != 0) {
                        Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = product.name,
                            modifier = Modifier
                                .size(100.dp)
                                .padding(end = 16.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(product.name, style = MaterialTheme.typography.h6)
                        Text(
                            product.description,
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            "${product.price} MAD",
                            style = MaterialTheme.typography.subtitle1,
                            modifier = Modifier.padding(top = 6.dp)
                        )

                        Button(
                            onClick = {
                                val item = CartItem(
                                    productId = product.id,
                                    name = product.name,
                                    price = product.price,
                                    imageName = product.imageName,
                                    quantity = 1
                                )
                                cartViewModel.addToCart(item)

                                scope.launch {
                                    snackbarHostState.showSnackbar("Ajouté au panier ✅")
                                }
                            },
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                        ) {
                            Text("🛒 Ajouter")
                        }
                    }
                }

                Button(
                    onClick = {
                        navController.navigate("details/${product.id}")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colors.primary),
                    elevation = ButtonDefaults.elevation(6.dp)
                ) {
                    Text(
                        "Voir détail du produit",
                        style = MaterialTheme.typography.button,
                        color = MaterialTheme.colors.onPrimary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp)
        )
    }
}

