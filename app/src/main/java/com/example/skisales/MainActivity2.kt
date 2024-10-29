package com.example.skisales

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Picasso

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var inv_id = ""
        val extras = intent.extras
        if (extras != null) {
            inv_id = extras.getString("inv_id").toString()
        }

        val db = Firebase.firestore
        val imageView = findViewById(R.id.imageView) as ImageView
        val textView_name = findViewById(R.id.textView) as TextView
        val textView_disc = findViewById(R.id.textView2) as TextView
        if(inv_id != null) {
            val docRef = db.collection("inv").document(inv_id)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                        Picasso.get().load("${document.data?.get("img")}").into(imageView)
                        textView_name.text = "${document.data?.get("name")}"
                        textView_disc.text = "${document.data?.get("description")}"
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }

        val fab: View = findViewById(R.id.floatingActionButton)
        fab.setOnClickListener { view ->

        }






    }
}