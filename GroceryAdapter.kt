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
import com.tenalis.mart.models.GroceryItem

class GroceryAdapter(
    val context: Context,
    val listener: OnItemClickListener,
    options: FirestoreRecyclerOptions<GroceryItem>
) : FirestoreRecyclerAdapter<GroceryItem, GroceryAdapter.GroceryHolder>(options) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroceryAdapter.GroceryHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(
            R.layout.item_grocery,
            parent, false
        )
        return GroceryHolder(v)
    }

    override fun onBindViewHolder(
        holder: GroceryAdapter.GroceryHolder,
        position: Int,
        model: GroceryItem
    ) {
        Picasso.get().load(model.image).placeholder(R.drawable.logo).into(holder.image)
        holder.name.text = model.name
        holder.price.text = "Price : ${model.price}/-"
        holder.originalPrice.paint.isStrikeThruText = true
        holder.originalPrice.text = model.realPrice

        if (model.outOfStock == "true") {
            holder.outOfStock.visibility = View.VISIBLE
        } else {
            holder.outOfStock.visibility = View.INVISIBLE
        }

        if (model.price.toInt() >= model.realPrice.toInt()) {
            holder.originalPrice.visibility = View.GONE
        } else {
            holder.originalPrice.visibility = View.VISIBLE
        }
    }

    inner class GroceryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val name: TextView = itemView.findViewById(R.id.title)
        val price: TextView = itemView.findViewById(R.id.price)
        val originalPrice: TextView = itemView.findViewById(R.id.realPrice)
        val outOfStock: TextView = itemView.findViewById(R.id.outOfStock)

        init {
            val auth = Firebase.auth
            val db = FirebaseFirestore.getInstance()
            itemView.setOnClickListener {
                val position = adapterPosition
                val item = getItem(position)
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

            itemView.setOnLongClickListener {
                val adminsRef = db.collection("admins")

                adminsRef.get().addOnSuccessListener { document ->
                    document.documents.forEach { uid ->
                        if (uid.get("uid").toString() == auth.currentUser?.uid) {
                            val position = adapterPosition
                            if (position != RecyclerView.NO_POSITION) {
                                val item = getItem(position)
                                listener.onLongClickListener(item)
                            }
                        }
                    }
                }
                return@setOnLongClickListener true
            }
        }
    }

    interface OnItemClickListener {
        fun onClickListener(item: GroceryItem)
        fun onLongClickListener(item: GroceryItem)
    }
}
