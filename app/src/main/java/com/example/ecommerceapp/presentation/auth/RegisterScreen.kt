package com.example.ecommerceapp.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ecommerceapp.R
import com.example.ecommerceapp.data.User
import com.example.ecommerceapp.viewmodel.UserViewModel

@Composable
fun RegisterScreen(navController: NavController, viewModel: UserViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    Image(
        painter = painterResource(id = R.drawable.store_background1), // change ici selon ton image
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.Center
    )



    {
        Text("Inscription", style = MaterialTheme.typography.h5)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nom complet") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") },
            modifier = Modifier.fillMaxWidth()
        )

        errorMessage?.let {
            Text(it, color = MaterialTheme.colors.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val user = User(email = email, password = password, name = name)
            viewModel.register(
                user,
                onSuccess = {
                    navController.navigate("home") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onError = { msg -> errorMessage = msg }
            )
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Créer un compte")
        }

        TextButton(onClick = {
            navController.navigate("login")
        }) {
            Text("Déjà un compte ? Se connecter")
        }
    }
}
