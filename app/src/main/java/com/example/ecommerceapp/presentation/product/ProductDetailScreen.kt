package com.example.ecommerceapp.presentation.product
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ecommerceapp.viewmodel.ProductViewModel
import com.example.ecommerceapp.viewmodel.CartViewModel
import com.example.ecommerceapp.data.local.entity.CartItem
import kotlinx.coroutines.launch

@Composable
fun ProductDetailScreen(
    productId: Int,
    productViewModel: ProductViewModel = viewModel(),
    navController: NavController,
    cartViewModel: CartViewModel = viewModel()
) {
    val product = productViewModel.products.collectAsState(initial = emptyList()).value
        .firstOrNull { it.id == productId }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    if (product != null) {
        val imageResId = context.resources.getIdentifier(
            product.imageName,
            "drawable",
            context.packageName
        )

        Scaffold(
            topBar = {
                TopAppBar(title = { Text(product.name) })
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (imageResId != 0) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = product.name,
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = product.name, style = MaterialTheme.typography.h5)
                Text(text = "${product.price} MAD", style = MaterialTheme.typography.h6)
                Text(text = product.description, style = MaterialTheme.typography.body1, modifier = Modifier.padding(top = 8.dp))

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val cartItem = CartItem(
                            productId = product.id,
                            name = product.name,
                            price = product.price,
                            imageName = product.imageName,
                            quantity = 1
                        )
                        cartViewModel.addToCart(cartItem)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Produit ajouté au panier ✅")
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🛒 Ajouter au panier")
                }
            }
        }
    } else {
        Text("Produit introuvable", modifier = Modifier.padding(16.dp))
    }
}

