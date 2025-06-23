package com.example.ecommerceapp.data.repository
import android.content.Context
import com.example.ecommerceapp.domain.Product
import com.example.ecommerceapp.utils.JsonUtils
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

class ProductRepository(private val context: Context) {
    fun loadProducts(): List<Product> {
        return try {
            val json = context.assets.open("products.json")
                .bufferedReader()
                .use { it.readText() }

            val productType = object : TypeToken<List<Product>>() {}.type
            Gson().fromJson(json, productType)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}

