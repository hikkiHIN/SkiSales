package com.example.skisales

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.skisales.MainActivity4
import com.google.firebase.Firebase
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Picasso

class MainActivity5 : AppCompatActivity() {

    private lateinit var listView: ListView
    private val items = mutableListOf<FirestoreItem>()
    private lateinit var adapter: FirestoreAdapter
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main5)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupListView()
        loadDataFromFirestore()
        val imageButton = findViewById<View?>(R.id.imageButton2)
        imageButton.setOnClickListener {
            val intent = Intent(this@MainActivity5, MainActivity7::class.java)
            startActivity(intent)
        }

    }

    private fun initViews() {
        listView = findViewById(R.id.listView)
        adapter = FirestoreAdapter(this, items)
        listView.adapter = adapter
    }

    private fun setupListView() {
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = items[position]
            Intent(this, DetailActivity::class.java).apply {
                putExtra("name", selectedItem.name)
                putExtra("documentId", selectedItem.documentId)
                startActivity(this)
            }
        }
    }

    private fun loadDataFromFirestore() {
        db.collection("inv")
            .get()
            .addOnSuccessListener { result ->
                items.clear()
                items.addAll(result.map { document ->
                    FirestoreItem.fromDocument(document)
                })
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Ошибка загрузки данных: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()
        // Обновляем данные при каждом возвращении на активити
        loadDataFromFirestore()
    }
}

data class FirestoreItem(
    val documentId: String,
    val name: String,
    val img: String? = null // Добавим поле для изображения, если нужно
) {
    companion object {
        fun fromDocument(document: QueryDocumentSnapshot): FirestoreItem {
            return FirestoreItem(
                documentId = document.id,
                name = document.getString("name") ?: "No name",
                img = document.getString("img")
            )
        }
    }
}

class FirestoreAdapter(
    context: Context,
    private val items: List<FirestoreItem>
) : ArrayAdapter<FirestoreItem>(context, R.layout.list_item, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_item, parent, false)

        val item = getItem(position) ?: return view

        // Устанавливаем имя
        view.findViewById<TextView>(R.id.itemName).text = item.name

        // Устанавливаем ID (или другую информацию)
        view.findViewById<TextView>(R.id.itemDescription).text = "ID: ${item.documentId}"

        // Если нужно отображать изображение (пример с Picasso)
        val imageView = view.findViewById<ImageView>(R.id.itemImage)
        item.img?.let { imageUrl ->
            Picasso.get()
                .load(imageUrl)
                //.placeholder(R.drawable.placeholder_image)
                //.error(R.drawable.error_image)
                .into(imageView)
        } //?: imageView.setImageResource(R.drawable.no_image)

        return view
    }
}