package com.tenalis.mart

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class AdminAddItemsActivity : AppCompatActivity() {

    private lateinit var itemName: EditText
    private lateinit var itemPrice: EditText
    private lateinit var mrp: EditText
    private lateinit var imageUrl: EditText
    private lateinit var progress: ProgressBar
    private lateinit var outOfStock: CheckBox

    private lateinit var mImageUri: Uri
    private lateinit var mStorageRef: StorageReference
    private lateinit var mUploadTask: StorageTask<*>
    private var image: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_items)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        itemName = findViewById(R.id.itemName)
        itemPrice = findViewById(R.id.itemPrice)
        mrp = findViewById(R.id.mrp)
        imageUrl = findViewById(R.id.imageUrl)
        outOfStock = findViewById(R.id.outOfStock)

        val uploadImage = findViewById<Button>(R.id.uploadImage)
        val uploadItem = findViewById<Button>(R.id.uploadItem)
        progress = findViewById(R.id.progressBar)

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads")

        uploadImage.setOnClickListener {
            openFileChooser()
        }

        uploadItem.setOnClickListener {
            if (itemName.text.toString().trim().isNotEmpty()) {
                if (itemPrice.text.toString().trim().isNotEmpty()) {
                    if (mrp.text.toString().trim().isNotEmpty()) {
                        if (imageUrl.text.toString().trim().isNotEmpty()) {
                            upload(
                                itemName.text.toString().trim(),
                                itemPrice.text.toString().trim(),
                                mrp.text.toString().trim(),
                                imageUrl.text.toString().trim(),
                                getCategory()
                            )
                        } else {
                            imageUrl.error = "Enter a Image Url or Upload a Image"
                        }
                    } else {
                        mrp.error = "Enter MRP"
                    }
                } else {
                    itemPrice.error = "Enter Price"
                }
            } else {
                itemName.error = "Enter a Valid Name"
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(intent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                mImageUri = result.data?.data!!
                progress.visibility = View.VISIBLE
                getImageUrl()
            }
        }

    private fun getFileExtension(uri: Uri): String? {
        val cR = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    private fun getImageUrl() {
        var bitmap: Bitmap? = null
        try {
            bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(
                    this.contentResolver,
                    mImageUri
                )
            } else {
                val source = ImageDecoder.createSource(this.contentResolver, mImageUri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val f = File(cacheDir, "temp")
        f.createNewFile()

        val bas = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 50, bas)
        val bitmapData: ByteArray = bas.toByteArray()

        val fos = FileOutputStream(f)
        fos.write(bitmapData)
        fos.flush()
        fos.close()

        val uri = Uri.fromFile(f)

        val fileReference: StorageReference = mStorageRef.child(
            System.currentTimeMillis()
                .toString() + "." + getFileExtension(uri)
        )

        mUploadTask = fileReference.putFile(uri)
            .addOnSuccessListener {
                fileReference.downloadUrl.addOnSuccessListener {
                    image = it.toString()
                    imageUrl.text = Editable.Factory.getInstance().newEditable(image)
                    progress.visibility = View.GONE
                }
                Toast.makeText(
                    this,
                    "Upload Successful",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error!",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("Storage", e.message.toString())
            }
    }

    private fun upload(
        name: String,
        price: String,
        realPrice: String,
        image: String,
        category: String
    ) {
        val db = FirebaseFirestore.getInstance()
        val itemRef =
            db.collection("items").document(category).collection("items").document()
        val data = hashMapOf(
            "id" to itemRef.id,
            "name" to name,
            "price" to price,
            "realPrice" to realPrice,
            "image" to image,
            "outOfStock" to outOfStock.isChecked.toString()
        )
        itemRef.set(data).addOnSuccessListener {
            itemName.text = Editable.Factory.getInstance().newEditable("")
            itemPrice.text = Editable.Factory.getInstance().newEditable("")
            mrp.text = Editable.Factory.getInstance().newEditable("")
            imageUrl.text = Editable.Factory.getInstance().newEditable("")
            Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCategory(): String {
        val category = findViewById<RadioGroup>(R.id.category).checkedRadioButtonId
        return findViewById<RadioButton>(category).text.toString()
    }
}
