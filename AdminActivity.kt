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

class AdminActivity : AppCompatActivity() {

    private lateinit var imageUrl: EditText
    private lateinit var progress: ProgressBar

    private lateinit var mImageUri: Uri
    private lateinit var mStorageRef: StorageReference
    private lateinit var mUploadTask: StorageTask<*>
    private var image: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val addItems = findViewById<Button>(R.id.addItems)

        val uploadImage = findViewById<Button>(R.id.uploadImage)
        val save = findViewById<Button>(R.id.save)
        imageUrl = findViewById(R.id.imageUrl)
        progress = findViewById(R.id.progressBar)

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads")

        uploadImage.setOnClickListener {
            openFileChooser()
        }

        addItems.setOnClickListener {
            startActivity(Intent(this, AdminAddItemsActivity::class.java))
        }

        save.setOnClickListener {
            if (imageUrl.text.isNotEmpty()) {
                val db = FirebaseFirestore.getInstance()
                val sliderRef =
                    db.collection("slider").document(getCategory())

                sliderRef.update("url", imageUrl.text.toString()).addOnSuccessListener {
                    Toast.makeText(this, "Successfully Updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                imageUrl.error = "Enter a Url"
            }
        }

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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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

    private fun getCategory(): String {
        val category = findViewById<RadioGroup>(R.id.category).checkedRadioButtonId
        return findViewById<RadioButton>(category).text.toString()
    }
}
