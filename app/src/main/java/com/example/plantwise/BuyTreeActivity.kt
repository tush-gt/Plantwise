package com.example.plantwise

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BuyTreeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: Button
    private val db = FirebaseFirestore.getInstance()
    private val treeList = mutableListOf<TreeDataModel>()
    private lateinit var adapter: TreeAdapter
    
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val isAdmin = currentUser?.email == "dbit@gmail.com"
    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_tree)

        recyclerView = findViewById(R.id.treeRecyclerView)
        addButton = findViewById(R.id.addTreeButton)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TreeAdapter(treeList, isAdmin)
        recyclerView.adapter = adapter

        if (isAdmin) {
            addButton.visibility = View.VISIBLE
            addButton.setOnClickListener {
                showAddTreeDialog()
            }
        } else {
            addButton.visibility = View.GONE
        }

        loadTreesFromFirebase()
    }

    private fun loadTreesFromFirebase() {
        db.collection("trees").get().addOnSuccessListener { result ->
            treeList.clear()
            for (doc in result) {
                treeList.add(doc.toObject(TreeDataModel::class.java).copy(id = doc.id))
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun showAddTreeDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_tree, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.treeNameInput)
        val priceInput = dialogView.findViewById<EditText>(R.id.treePriceInput)
        val selectImageButton = dialogView.findViewById<Button>(R.id.selectImageButton)
        selectedImageView = dialogView.findViewById(R.id.selectedImageView)
        val submitButton = dialogView.findViewById<Button>(R.id.addTreeSubmitButton)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        submitButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val priceText = priceInput.text.toString().trim()
            val price = priceText.toDoubleOrNull()

            if (name.isEmpty() || price == null || selectedImageUri == null) {
                Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val config = hashMapOf(
                "cloud_name" to "dztzai54z",
                "api_key" to "265654958636919",
                "api_secret" to "d9LlXTITp3hm2PUEVU_bcEu1-s4" // 🛑 Replace this with your real API secret
            )

            val cloudinary = Cloudinary(config)

            lifecycleScope.launch {
                try {
                    val inputStream = withContext(Dispatchers.IO) {
                        contentResolver.openInputStream(selectedImageUri!!)
                    }

                    val uploadResult = withContext(Dispatchers.IO) {
                        cloudinary.uploader().upload(inputStream, ObjectUtils.emptyMap())
                    }

                    val imageUrl = uploadResult["secure_url"] as String
                    val tree = TreeDataModel(name = name, price = price, imageUrl = imageUrl)

                    db.collection("trees").add(tree)
                        .addOnSuccessListener {
                            Toast.makeText(this@BuyTreeActivity, "Tree added successfully", Toast.LENGTH_SHORT).show()
                            loadTreesFromFirebase()
                            dialog.dismiss()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@BuyTreeActivity, "Failed to save tree", Toast.LENGTH_SHORT).show()
                        }

                } catch (e: Exception) {
                    Toast.makeText(this@BuyTreeActivity, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            selectedImageView?.setImageURI(selectedImageUri)
            selectedImageView?.visibility = View.VISIBLE
        }
    }
}
