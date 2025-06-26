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
import androidx.navigation.NavController
import com.example.ecommerceapp.domain.Product
import com.example.ecommerceapp.data.local.entity.CartItem
import com.example.ecommerceapp.viewmodel.CartViewModel
import kotlinx.coroutines.launch

@Composable
fun ProductCard(
    product: Product,
    navController: NavController,
    cartViewModel: CartViewModel,
    modifier: Modifier
) {
    val context = LocalContext.current
    val imageResId = context.resources.getIdentifier(
        product.imageName, "drawable", context.packageName
    )

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            if (imageResId != 0) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(bottom = 8.dp)
                )
            }

            Text(
                text = product.name,
                style = MaterialTheme.typography.h6
            )

            Text(
                text = product.description,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                text = "${product.price} MAD",
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                ) {
                    Text("🛒+")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { navController.navigate("details/${product.id}") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
                ) {
                    Text("🔍")
                }
            }
        }
    }

    // Snackbar global (hors Card)
    Box(modifier = Modifier.fillMaxWidth()) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp)
        )
    }
}


