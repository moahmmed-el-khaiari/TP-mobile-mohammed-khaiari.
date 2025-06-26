package com.example.ecommerceapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_prefs")

object SessionManager {
    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    private val USER_EMAIL = stringPreferencesKey("user_email")

    suspend fun setLoginState(context: Context, loggedIn: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = loggedIn
        }
    }

    fun isLoggedIn(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[IS_LOGGED_IN] ?: false
        }
    }

    suspend fun saveUserEmail(context: Context, email: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_EMAIL] = email
        }
    }

    fun getUserEmail(context: Context): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[USER_EMAIL]
        }
    }

    suspend fun clearSession(context: Context) {
        context.dataStore.edit { it.clear() }
    }
}
