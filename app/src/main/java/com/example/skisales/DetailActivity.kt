package com.example.skisales

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class DetailActivity : AppCompatActivity() {

    private lateinit var documentId: String
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Получаем данные из Intent
        documentId = intent.getStringExtra("documentId") ?: ""
        val name = intent.getStringExtra("name") ?: ""

        // Инициализация UI элементов
        val titleInput = findViewById<EditText>(R.id.titleInput)
        val descriptionInput = findViewById<EditText>(R.id.descriptionInput)
        val priceInput = findViewById<EditText>(R.id.priceInput)
        val imageUrlInput = findViewById<EditText>(R.id.imageUrlInput)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val deleteButton = findViewById<Button>(R.id.deleteButton)
        val detailImage = findViewById<ImageView>(R.id.detailImage)

        // Устанавливаем название в заголовок
        titleInput.setText(name)

        // Загружаем данные документа
        loadDocumentData(titleInput, descriptionInput, priceInput, imageUrlInput, detailImage)

        // Обработчик кнопки сохранения
        saveButton.setOnClickListener {
            updateDocument(
                titleInput.text.toString(),
                descriptionInput.text.toString(),
                priceInput.text.toString(),
                imageUrlInput.text.toString(),
                detailImage
            )
        }

        // Обработчик кнопки удаления
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        // Обновляем изображение при изменении URL
        imageUrlInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                loadImage(imageUrlInput.text.toString(), detailImage)
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Удаление товара")
            .setMessage("Вы уверены, что хотите удалить этот товар?")
            .setPositiveButton("Удалить") { _, _ ->
                deleteDocument()
            }
            .setNegativeButton("Отмена", null)
            .create()
            .show()
    }

    private fun deleteDocument() {
        db.collection("inv").document(documentId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Товар успешно удалён", Toast.LENGTH_SHORT).show()
                finish() // Закрываем активити после удаления
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка при удалении: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadDocumentData(
        titleInput: EditText,
        descriptionInput: EditText,
        priceInput: EditText,
        imageUrlInput: EditText,
        imageView: ImageView
    ) {
        db.collection("inv").document(documentId)
            .get()
            .addOnSuccessListener { document ->
                titleInput.setText(document.getString("name"))
                descriptionInput.setText(document.getString("description"))
                priceInput.setText(document.getDouble("price")?.toString() ?: "0.0")

                val imageUrl = document.getString("img") ?: ""
                imageUrlInput.setText(imageUrl)
                loadImage(imageUrl, imageView)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateDocument(
        title: String,
        description: String,
        priceStr: String,
        imageUrl: String,
        imageView: ImageView
    ) {
        val price = try {
            priceStr.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Некорректная цена", Toast.LENGTH_SHORT).show()
            return
        }

        val updates = hashMapOf<String, Any>(
            "name" to title,
            "description" to description,
            "price" to price,
            "img" to imageUrl
        )

        db.collection("inv").document(documentId)
            .update(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Изменения сохранены", Toast.LENGTH_SHORT).show()
                loadImage(imageUrl, imageView)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка сохранения: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadImage(url: String, imageView: ImageView) {
        if (url.isNotEmpty()) {
            Glide.with(this)
                .load(url)
                //.placeholder(R.drawable.placeholder_image)
                //.error(R.drawable.error_image)
                .into(imageView)
        } else {
            //imageView.setImageResource(R.drawable.no_image)
        }
    }
}