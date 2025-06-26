package com.example.ecommerceapp.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ecommerceapp.presentation.components.ProductCard
import com.example.ecommerceapp.viewmodel.CartViewModel
import com.example.ecommerceapp.viewmodel.OrderViewModel
import com.example.ecommerceapp.viewmodel.ProductViewModel
import com.example.ecommerceapp.viewmodel.OrderViewModelFactory
import com.example.ecommerceapp.data.local.AppDatabase
import com.example.ecommerceapp.data.repository.OrderRepository

enum class SortType { ASCENDING, DESCENDING }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    productViewModel: ProductViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val context = LocalContext.current
    val orderViewModel: OrderViewModel = viewModel(
        factory = OrderViewModelFactory(
            OrderRepository(
                AppDatabase.getDatabase(context).orderDao(),
                AppDatabase.getDatabase(context).orderItemDao()
            )
        )
    )

    val allProducts by productViewModel.products.collectAsState(initial = emptyList())
    val orders by orderViewModel.orders.collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var sortType by remember { mutableStateOf<SortType?>(null) }
    var selectedCategory by remember { mutableStateOf("Toutes") }
    var categoryExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        orderViewModel.loadOrders()
    }

    val categories = remember(allProducts) {
        listOf("Toutes") + allProducts.map { it.category }.distinct()
    }

    val filteredProducts = allProducts
        .filter { it.name.contains(searchQuery, ignoreCase = true) }
        .filter { selectedCategory == "Toutes" || it.category == selectedCategory }
        .let {
            when (sortType) {
                SortType.ASCENDING -> it.sortedBy { product -> product.price }
                SortType.DESCENDING -> it.sortedByDescending { product -> product.price }
                else -> it
            }
        }

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFFFEF9F8), Color(0xFFE3F2FD))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "🛍️ ShopEase",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                },

                actions = {


                       IconButton(onClick = { navController.navigate("profile") }) {
                           Icon(
                               imageVector = Icons.Default.AccountCircle, // nécessite cet import
                               contentDescription = "Profil",
                               tint = Color.White
                           )
                       }
                    IconButton(onClick = { navController.navigate("cart") }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Panier", tint = Color.White)
                    }
                },
                backgroundColor = Color(0xFF1976D2),
                elevation = 10.dp
            )
        },
        backgroundColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .background(gradientBackground)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Rechercher", tint = Color.Gray)
                },
                placeholder = { Text("Rechercher un produit...", color = Color.Gray) },
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF1976D2),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    cursorColor = Color(0xFF1976D2)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    Button(
                        onClick = { categoryExpanded = true },
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White,
                            contentColor = Color(0xFF1976D2)
                        )
                    ) {
                        Text("Catégorie: $selectedCategory", fontSize = 14.sp)
                    }

                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(onClick = {
                                selectedCategory = category
                                categoryExpanded = false
                            }) {
                                Text(
                                    category,
                                    fontWeight = if (category == selectedCategory) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Button(onClick = {
                    sortType = if (sortType == SortType.ASCENDING) null else SortType.ASCENDING
                }) {
                    Text(if (sortType == SortType.ASCENDING) "Annuler ↑" else "Prix ↑")
                }

                Button(onClick = {
                    sortType = if (sortType == SortType.DESCENDING) null else SortType.DESCENDING
                }) {
                    Text(if (sortType == SortType.DESCENDING) "Annuler ↓" else "Prix ↓")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (orders.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { navController.navigate("orderHistory") }
                    ) {
                        val orderCount = orders.size
                        Text(
                            text = "📦 Historique des commandes ($orderCount)",
                            color = Color(0xFF1976D2),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }



            // 🔥 Grille de produits
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredProducts, key = { it.id }) { product ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        ProductCard(
                            product = product,
                            navController = navController,
                            cartViewModel = cartViewModel,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}
