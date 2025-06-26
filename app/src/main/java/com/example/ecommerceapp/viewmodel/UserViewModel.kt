package com.example.ecommerceapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.User
import com.example.ecommerceapp.data.local.AppDatabase
import com.example.ecommerceapp.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val dao = AppDatabase.getDatabase(context).userDao()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    init {
        // Chargement automatique de la session
        viewModelScope.launch {
            SessionManager.isLoggedIn(context).collect { loggedIn ->
                _isLoggedIn.value = loggedIn
                if (loggedIn) {
                    val email = SessionManager.getUserEmail(context).first()
                    if (email != null) {
                        _currentUser.value = dao.getUserByEmail(email)
                    }
                }
            }
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val user = dao.login(email, password)
            if (user != null) {
                _currentUser.value = user
                _isLoggedIn.value = true
                SessionManager.setLoginState(context, true)
                SessionManager.saveUserEmail(context, email)
                onSuccess()
            } else {
                onError("Email ou mot de passe incorrect")
            }
        }
    }

    fun register(user: User, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                dao.register(user)
                _currentUser.value = user
                _isLoggedIn.value = true
                SessionManager.setLoginState(context, true)
                SessionManager.saveUserEmail(context, user.email)
                onSuccess()
            } catch (e: Exception) {
                onError("Erreur lors de l'enregistrement")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            SessionManager.clearSession(context)
            _currentUser.value = null
            _isLoggedIn.value = false
        }
    }
}
