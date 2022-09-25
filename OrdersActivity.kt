package com.tenalis.mart

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.tenalis.mart.adapters.OrdersAdapter
import com.tenalis.mart.models.OrderItem

class OrdersActivity : AppCompatActivity(), OrdersAdapter.OnItemClickListener {

    private val db = FirebaseFirestore.getInstance()
    private val ordersRef =
        db.collection("orders")
    private lateinit var adapter: OrdersAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var layout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val query: Query = ordersRef.orderBy("id", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<OrderItem>()
            .setQuery(query, OrderItem::class.java)
            .build()

        adapter = OrdersAdapter(this, this, options)
        recyclerView = findViewById(R.id.ordersView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        layout = findViewById(R.id.layout)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClickListener(item: OrderItem) {
        val intent = Intent(this, CartActivity::class.java)
        intent.putExtra("uid", item.userId)
        intent.putExtra("fromOrders", true)
        intent.putExtra("name", item.name)
        startActivity(intent)
    }

    override fun onLongClickListener(item: OrderItem) {
        AlertDialog.Builder(this)
            .setTitle("Delete")
            .setMessage("Are you sure you want to delete")
            .setPositiveButton("Delete") { dialogInterface: DialogInterface, _: Int ->
                ordersRef.document(item.id).delete()
                Snackbar.make(layout, "Item Deleted", Snackbar.LENGTH_LONG).setAction("UNDO") {
                    ordersRef.document(item.id).set(item).addOnSuccessListener {
                        Snackbar.make(layout, "Item Restored", Snackbar.LENGTH_LONG).show()
                    }
                }.show()
                dialogInterface.dismiss()
            }.setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }.show()
    }
}
