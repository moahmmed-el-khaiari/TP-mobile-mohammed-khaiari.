package com.example.ecommerceapp.utils
import android.content.Context
import com.example.ecommerceapp.domain.Product
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

object JsonUtils {
    fun parseProductsFromJson(context: Context): List<Product> {
        val jsonString = context.assets.open("products.json").bufferedReader().use { it.readText() }
        val listProductType = object : TypeToken<List<Product>>() {}.type
        return Gson().fromJson(jsonString, listProductType)
    }
}
