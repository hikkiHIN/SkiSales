package com.example.skisales

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Button
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.google.firebase.Firebase
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Picasso

class MainActivity4 : AppCompatActivity() {

    private lateinit var mainParentVertLayout: LinearLayout
    private val db = Firebase.firestore
    private val imageViews = mutableListOf<ImageView>()
    private val textViews = mutableListOf<TextView>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main4)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mainParentVertLayout = findViewById(R.id.linlayvert)
        val button = findViewById<Button>(R.id.button)
        val username = intent.getStringExtra("username") ?: ""
        val isAdmin = intent.getBooleanExtra("isAdmin", false)
        if (!isAdmin){
            button.isEnabled = false
            button.isVisible = false
        }

        button.setOnClickListener {
            val intent = Intent(this@MainActivity4, MainActivity5::class.java)
            startActivity(intent)
        }

        loadDataFromFirestore()
    }

    private fun loadDataFromFirestore() {
        // Очищаем предыдущие данные
        mainParentVertLayout.removeAllViews()
        imageViews.clear()
        textViews.clear()

        db.collection("inv")
            .get()
            .addOnSuccessListener { result ->
                Log.d(TAG, "Loaded ${result.size()} documents")

                var currentImageRow: LinearLayout? = null
                var currentTextRow: LinearLayout? = null
                var itemCounter = 0

                for (document in result) {
                    // Создаем новую строку каждые 3 элемента
                    if (itemCounter % 3 == 0) {
                        val verticalLayout = LinearLayout(this).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            orientation = LinearLayout.VERTICAL
                        }

                        currentImageRow = LinearLayout(this).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            orientation = LinearLayout.HORIZONTAL
                        }

                        currentTextRow = LinearLayout(this).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            orientation = LinearLayout.HORIZONTAL
                        }

                        verticalLayout.addView(currentImageRow)
                        verticalLayout.addView(currentTextRow)
                        mainParentVertLayout.addView(verticalLayout)
                    }

                    // Создаем ImageView
                    val imageView = ImageView(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f
                        ).apply {
                            setMargins(4, 4, 4, 4)
                        }
                        maxHeight = 500
                        adjustViewBounds = true
                        setOnClickListener {
                            Intent(applicationContext, MainActivity2::class.java).apply {
                                putExtra("inv_id", document.id)
                                startActivity(this)
                            }
                        }
                    }
                    currentImageRow?.addView(imageView)
                    imageViews.add(imageView)

                    // Создаем TextView
                    val textView = TextView(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f
                        )
                        gravity = Gravity.CENTER
                        textSize = 15f
                    }
                    currentTextRow?.addView(textView)
                    textViews.add(textView)

                    // Загружаем данные
                    updateItemView(itemCounter, document)
                    itemCounter++
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents", exception)
                Toast.makeText(applicationContext, "Произошла ошибка получения данных", Toast.LENGTH_LONG).show()
            }
    }

    private fun updateItemView(position: Int, document: QueryDocumentSnapshot) {
        // Обновляем изображение
        Picasso.get()
            .load(document.getString("img"))
            //.placeholder(R.drawable.placeholder_image)
            //.error(R.drawable.error_image)
            .into(imageViews[position])

        // Обновляем текст
        textViews[position].text = document.getString("name")
    }

    override fun onResume() {
        super.onResume()
        // Обновляем данные при возвращении на активити
        loadDataFromFirestore()
    }

    companion object {
        private const val TAG = "MainActivity4"
    }
}