package com.tenalis.mart

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class AddActivity : AppCompatActivity() {

    private val auth = Firebase.auth
    private val db = FirebaseFirestore.getInstance()
    private val sliderRef = db.collection("slider")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val cartRef =
            db.collection("users").document(auth.currentUser?.uid!!).collection("cart")

        val id = intent.getStringExtra("id")!!
        val category = intent.getStringExtra("category")!!
        val name = intent.getStringExtra("name")!!
        val imageUrl = intent.getStringExtra("imageUrl")!!
        val price = intent.getStringExtra("price")!!
        val mrp = intent.getStringExtra("mrp")!!

        val title = findViewById<TextView>(R.id.title)
        val image = findViewById<ImageView>(R.id.image)
        val ad1 = findViewById<ImageView>(R.id.ad1)
        val priceView = findViewById<TextView>(R.id.price)
        val realPrice = findViewById<TextView>(R.id.realPrice)

        val add = findViewById<Button>(R.id.add)

        title.text = name
        Picasso.get().load(imageUrl).placeholder(R.drawable.logo).into(image)
        priceView.text = "Price : $price/-"

        realPrice.paint.isStrikeThruText = true
        realPrice.text = mrp

        sliderRef.document("Ad1").addSnapshotListener { value, error ->
            if (error == null) {
                Picasso.get().load(value?.get("url").toString()).into(ad1)
            }
        }

        add.setOnClickListener {
            val data = hashMapOf(
                "id" to id,
                "count" to "1",
                "category" to category
            )
            cartRef.document(id).set(data)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Added Successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Error ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
