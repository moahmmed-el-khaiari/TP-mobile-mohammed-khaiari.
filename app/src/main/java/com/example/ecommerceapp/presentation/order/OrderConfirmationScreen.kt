import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ecommerceapp.viewmodel.CartViewModel

@Composable
fun OrderConfirmationScreen(
    navController: NavController,
    viewModel: CartViewModel = viewModel()
) {
    val receipt by viewModel.receipt.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Confirmation de la commande") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Merci pour votre commande !", style = MaterialTheme.typography.h6)

            Spacer(modifier = Modifier.height(8.dp))

            if (receipt != null) {
                Text(text = receipt!!)
            } else {
                Text(text = "bein recus votre comamnde .")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Retour à l'accueil")
            }
        }
    }
}
