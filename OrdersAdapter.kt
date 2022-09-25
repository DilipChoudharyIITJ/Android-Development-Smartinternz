package com.tenalis.mart.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.tenalis.mart.R
import com.tenalis.mart.models.OrderItem

class OrdersAdapter(
    val context: Context,
    val listener: OnItemClickListener,
    options: FirestoreRecyclerOptions<OrderItem>
) : FirestoreRecyclerAdapter<OrderItem, OrdersAdapter.OrderHolder>(options) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrdersAdapter.OrderHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(
            R.layout.item_orders,
            parent, false
        )
        return OrderHolder(v)
    }

    override fun onBindViewHolder(
        holder: OrdersAdapter.OrderHolder,
        position: Int,
        model: OrderItem
    ) {
        holder.name.text = "Order By : ${model.name}"
        holder.address.text = model.address
        holder.price.text = "Total Price : ${model.total}/-"
        holder.phoneNumber.text =
            "Phone Number : ${model.phoneNumber.replace("+91", "")}"
    }

    inner class OrderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val address: TextView = itemView.findViewById(R.id.address)
        val price: TextView = itemView.findViewById(R.id.totalPrice)
        val phoneNumber: TextView = itemView.findViewById(R.id.phoneNumber)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    listener.onClickListener(item)
                }
            }

            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    listener.onLongClickListener(item)
                }
                return@setOnLongClickListener true
            }
        }
    }

    interface OnItemClickListener {
        fun onClickListener(item: OrderItem)
        fun onLongClickListener(item: OrderItem)
    }
}
