package com.example.ecommerceapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val items: String, // Liste des IDs de produits commandés, ex: "[1,2,3]"
    val totalAmount: Double,
    val status: String = "EN_ATTENTE", // Statut de la commande
    val date: Long = System.currentTimeMillis()
)
