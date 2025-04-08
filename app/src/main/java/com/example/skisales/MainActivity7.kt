package com.example.skisales

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity7 : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var priceInput: EditText
    private lateinit var imageUrlInput: EditText
    private lateinit var detailImage: ImageView
    private lateinit var saveButton: Button
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main7)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Инициализация views
        titleInput = findViewById(R.id.titleInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        priceInput = findViewById(R.id.priceInput)
        imageUrlInput = findViewById(R.id.imageUrlInput)
        detailImage = findViewById(R.id.detailImage)
        saveButton = findViewById(R.id.saveButton)

        saveButton.setOnClickListener {
            addProductToFirestore()
        }

        // Обновление превью изображения при изменении URL
        imageUrlInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                loadImage(imageUrlInput.text.toString())
            }
        }
    }

    private fun addProductToFirestore() {
        val name = titleInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()
        val priceText = priceInput.text.toString().trim()
        val imageUrl = imageUrlInput.text.toString().trim()

        if (name.isEmpty() || description.isEmpty() || priceText.isEmpty()) {
            Toast.makeText(this, "Заполните все обязательные поля", Toast.LENGTH_SHORT).show()
            return
        }

        val price = try {
            priceText.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Некорректная цена", Toast.LENGTH_SHORT).show()
            return
        }

        val product = hashMapOf(
            "name" to name,
            "description" to description,
            "price" to price,
            "img" to imageUrl
        )

        db.collection("inv")
            .add(product)
            .addOnSuccessListener {
                Toast.makeText(this, "Товар успешно добавлен", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка при добавлении: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadImage(url: String) {
        if (url.isNotEmpty()) {
            Glide.with(this)
                .load(url)
                //.placeholder(R.drawable.placeholder_image)
                //.error(R.drawable.error_image)
                .into(detailImage)
        } else {
            //detailImage.setImageResource(R.drawable.no_image)
        }
    }
}