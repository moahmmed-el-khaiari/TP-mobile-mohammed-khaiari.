package com.example.ecommerceapp.presentation.order

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ecommerceapp.data.local.AppDatabase
import com.example.ecommerceapp.data.repository.OrderRepository
import com.example.ecommerceapp.domain.OrderDomain
import com.example.ecommerceapp.viewmodel.OrderViewModel
import com.example.ecommerceapp.viewmodel.OrderViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrderHistoryScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val db = AppDatabase.getDatabase(application)
    val orderRepository = remember { OrderRepository(db.orderDao(), db.orderItemDao()) }

    val orderViewModel: OrderViewModel = viewModel(
        factory = OrderViewModelFactory(orderRepository)
    )

    val orders by orderViewModel.orders.collectAsState()

    LaunchedEffect(Unit) {
        orderViewModel.loadOrders()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Historique des commandes") })
        }
    ) { padding ->
        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Aucune commande trouvée.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { order ->
                    OrderHistoryItem(order = order) {
                        navController.navigate("orderDetail/${order.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun OrderHistoryItem(order: OrderDomain, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Commande #${order.id}", style = MaterialTheme.typography.h6)
            Text("Date : ${formatDate(order.timestamp)}", style = MaterialTheme.typography.body2)
            Text("Statut : ${order.status.name.replace("_", " ")}", style = MaterialTheme.typography.body2)
            Text("Total : ${order.total} €", style = MaterialTheme.typography.body1)
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
