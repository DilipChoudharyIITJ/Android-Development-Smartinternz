package com.tenalis.mart

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.tenalis.mart.adapters.SectionsPagerAdapter


class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private val auth = Firebase.auth

    private val db = FirebaseFirestore.getInstance()
    private val adminsRef = db.collection("admins")
    private val sliderRef = db.collection("slider")

    private val tabTitles = arrayOf(
        R.string.tab_text_1,
        R.string.tab_text_2,
        R.string.tab_text_3,
        R.string.tab_text_4,
        R.string.tab_text_5,
        R.string.tab_text_6,
        R.string.tab_text_7,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sectionsPagerAdapter = SectionsPagerAdapter(lifecycle, supportFragmentManager)
        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = getString(tabTitles[position])
        }.attach()
        val fab: FloatingActionButton = findViewById(R.id.fab)

        fab.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        val imageView = ImageView(this)
        sliderRef.document("Ad2").addSnapshotListener { value, error ->
            if (error == null) {
                Picasso.get().load(value?.get("url").toString()).into(imageView)
                AlertDialog.Builder(this).setView(imageView).show()
            }
        }
    }

    fun navigateToPage(i: Int) {
        viewPager.currentItem = i
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        adminsRef.get().addOnSuccessListener { document ->
            document.documents.forEach { uid ->
                if (uid.get("uid").toString() == auth.currentUser?.uid) {
                    menu.findItem(R.id.admin).isVisible = true
                    menu.findItem(R.id.orders).isVisible = true
                }
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.phone -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CALL_PHONE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CALL_PHONE), 1
                    )
                } else {
                    AlertDialog.Builder(this)
                        .setTitle("Call")
                        .setMessage("Are you sure you want to call us")
                        .setPositiveButton("Call") { dialogInterface: DialogInterface, _: Int ->
                            val intent = Intent(Intent.ACTION_CALL)
                            intent.data = Uri.parse("tel:${App.PhoneNumber}")
                            startActivity(intent)
                            dialogInterface.dismiss()
                        }.setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
                            dialogInterface.dismiss()
                        }.show()
                }
                true
            }
            R.id.whatsApp -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://api.whatsapp.com/send?phone=+91${App.PhoneNumber}")
                startActivity(intent)
                true
            }
            R.id.logOut -> {
                Firebase.auth.signOut()
                startActivity(Intent(this, StartActivity::class.java))
                finish()
                true
            }
            R.id.admin -> {
                startActivity(Intent(this, AdminActivity::class.java))
                true
            }
            R.id.orders -> {
                startActivity(Intent(this, OrdersActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
