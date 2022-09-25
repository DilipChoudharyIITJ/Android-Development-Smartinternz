package com.tenalis.mart

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.tenalis.mart.models.GroceryItem

class App : AppCompatActivity() {

    companion object {
        const val PhoneNumber = "9390345080"

        private val db = FirebaseFirestore.getInstance()

        val foodRef = db.collection("items").document("food").collection("items")
        val groceryRef = db.collection("items").document("grocery").collection("items")
        val beverageRef = db.collection("items").document("beverage").collection("items")
        val detergentsRef = db.collection("items").document("detergents").collection("items")
        val kitchenRef = db.collection("items").document("kitchen").collection("items")
        val otherRef = db.collection("items").document("other").collection("items")

        fun onClickItems(
            item: GroceryItem,
            context: Context,
            category: String
        ) {
            val intent = Intent(context, AddActivity::class.java)
            intent.putExtra("category", category)
            intent.putExtra("id", item.id)
            intent.putExtra("name", item.name)
            intent.putExtra("imageUrl", item.image)
            intent.putExtra("price", item.price)
            intent.putExtra("mrp", item.realPrice)
            context.startActivity(intent)
        }
    }
}
