package com.tenalis.mart.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.tenalis.mart.R
import com.tenalis.mart.models.CartItem

class CartAdapter(
    val context: Context,
    val listener: OnItemClickListener,
    options: FirestoreRecyclerOptions<CartItem>
) : FirestoreRecyclerAdapter<CartItem, CartAdapter.CartHolder>(options) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartAdapter.CartHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(
            R.layout.item_cart,
            parent, false
        )
        return CartHolder(v)
    }

    override fun onBindViewHolder(
        holder: CartAdapter.CartHolder,
        position: Int,
        model: CartItem
    ) {
        val db = FirebaseFirestore.getInstance()
        val itemRef =
            db.collection("items").document(model.category).collection("items").document(model.id)

        itemRef.get().addOnSuccessListener {
            if (it.exists()) {
                Picasso.get()
                    .load(it.getString("image").toString())
                    .placeholder(R.drawable.logo)
                    .into(holder.image)

                holder.name.text = it.getString("name").toString()
                holder.price.text = "Price : ${it.getString("price").toString()}/-"
                holder.originalPrice.paint.isStrikeThruText = true
                holder.originalPrice.text = it.getString("realPrice").toString()
                holder.counter.text = "Quantity : ${model.count}"

                if (it.getString("price").toString().toInt() >= it.getString("realPrice").toString()
                        .toInt()
                ) {
                    holder.originalPrice.visibility = View.GONE
                } else {
                    holder.originalPrice.visibility = View.VISIBLE
                }
            }
        }
    }

    inner class CartHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val name: TextView = itemView.findViewById(R.id.title)
        val price: TextView = itemView.findViewById(R.id.price)
        val originalPrice: TextView = itemView.findViewById(R.id.realPrice)
        val counter: TextView = itemView.findViewById(R.id.count)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                val item = getItem(position)
                val auth = Firebase.auth
                val db = FirebaseFirestore.getInstance()
                val ordersRef =
                    db.collection("orders")
                var notAllowOnclick = false

                ordersRef.get().addOnSuccessListener { document ->
                    document.documents.forEach { order ->
                        if (order.get("userId").toString() == auth.currentUser?.uid) {
                            notAllowOnclick = true
                            Toast.makeText(
                                context,
                                order.get("userId").toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    if (position != RecyclerView.NO_POSITION && !notAllowOnclick) {
                        listener.onClickListener(item)
                    } else if (notAllowOnclick) {
                        Toast.makeText(
                            context,
                            "You can't order until your order is delivered",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onClickListener(item: CartItem)
    }
}
