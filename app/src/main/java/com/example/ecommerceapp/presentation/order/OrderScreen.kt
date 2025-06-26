package com.example.ecommerceapp.presentation.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecommerceapp.domain.OrderDomain
import com.example.ecommerceapp.viewmodel.OrderViewModel

@Composable
fun OrderScreen(
    orderViewModel: OrderViewModel = viewModel(),
    onOrderClick: (Int) -> Unit // id de la commande cliquée
) {
    val orders by orderViewModel.orders.collectAsState()
    val isLoading by orderViewModel.isLoading.collectAsState()
    val errorMessage by orderViewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        orderViewModel.loadOrders()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mes commandes") })
        },
        content = { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    errorMessage != null -> {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colors.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    orders.isEmpty() -> {
                        Text(
                            text = "Aucune commande trouvée",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(orders) { order ->
                                OrderListItem(order = order, onClick = { onOrderClick(order.id) })
                                Divider()
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun OrderListItem(order: OrderDomain, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Text(text = "Commande #${order.id}", style = MaterialTheme.typography.h6)
        Text(text = "Total: ${order.total} €")
        Text(text = "Statut: ${order.status.name.replace('_', ' ')}")
        Text(text = "Date: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(java.util.Date(order.timestamp))}")
    }
}
