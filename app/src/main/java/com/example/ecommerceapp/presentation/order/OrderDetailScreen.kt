package com.example.ecommerceapp.presentation.order

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.ecommerceapp.data.local.AppDatabase
import com.example.ecommerceapp.data.repository.OrderRepository
import com.example.ecommerceapp.domain.OrderDomain
import com.example.ecommerceapp.viewmodel.OrderViewModel
import com.example.ecommerceapp.viewmodel.OrderViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrderDetailScreen(
    orderId: Int,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val db = AppDatabase.getDatabase(application)
    val orderRepository = remember { OrderRepository(db.orderDao(), db.orderItemDao()) }
    val orderViewModel: OrderViewModel = viewModel(factory = OrderViewModelFactory(orderRepository))

    val orderState = remember { mutableStateOf<OrderDomain?>(null) }
    val isLoading = remember { mutableStateOf(true) }
    val error = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(orderId) {
        try {
            isLoading.value = true
            val order = orderViewModel.getOrderById(orderId)
            orderState.value = order
        } catch (e: Exception) {
            error.value = e.message
        } finally {
            isLoading.value = false
        }
    }

    when {
        isLoading.value -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        error.value != null -> {
            Text(
                text = error.value ?: "Erreur inconnue",
                color = MaterialTheme.colors.error,
                modifier = Modifier.padding(16.dp)
            )
        }
        orderState.value != null -> {
            OrderDetailContent(order = orderState.value!!, onBack = onBack)
        }
        else -> {
            Text("Commande introuvable", modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun OrderDetailContent(order: OrderDomain, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Détails de la commande #${order.id}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Date : ${formatDate(order.timestamp)}", style = MaterialTheme.typography.body2)
            Text(
                "Statut : ${order.status.name.replace('_', ' ')}",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text("Produits :", style = MaterialTheme.typography.subtitle1)

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(order.items) { item ->
                    OrderItemRow(
                        itemName = item.name,
                        quantity = item.quantity,
                        price = item.price,
                        imageUrl = item.imageUrl
                    )
                    Divider()
                }
            }

            Text(
                "Total : ${order.total} €",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }
    }
}

@Composable
fun OrderItemRow(itemName: String, quantity: Int, price: Double, imageUrl: String) {
    val context = LocalContext.current

    val imageResId = remember(imageUrl) {
        context.resources.getIdentifier(
            imageUrl.substringBeforeLast('.'),
            "drawable",
            context.packageName
        ).takeIf { it != 0 } ?: android.R.drawable.ic_menu_report_image // fallback si image manquante
    }

    val painter = rememberAsyncImagePainter(imageResId)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painter,
            contentDescription = itemName,
            modifier = Modifier.size(64.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(itemName, style = MaterialTheme.typography.subtitle1)
            Text("Quantité: $quantity", style = MaterialTheme.typography.body2)
        }
        Text(
            text = String.format("%.2f €", price),
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.alignByBaseline()
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
