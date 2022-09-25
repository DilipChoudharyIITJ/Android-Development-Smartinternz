package com.tenalis.mart.models

data class GroceryItem(
    val id: String = "",
    val name: String = "Item",
    val image: String = "https://upload.wikimedia.org/wikipedia/commons/f/f0/Error.svg",
    val price: String = "UnKnown",
    val realPrice: String = "UnKnown",
    val outOfStock: String = "false"
)
