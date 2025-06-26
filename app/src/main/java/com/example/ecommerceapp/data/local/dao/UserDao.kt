package com.example.ecommerceapp.data.local.dao

import androidx.room.*
import com.example.ecommerceapp.data.User

@Dao
interface UserDao {
    @Insert
    suspend fun register(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): User?
}
