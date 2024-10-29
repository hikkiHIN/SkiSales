package com.example.skisales

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Picasso


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = Firebase.firestore // init db
        val layout: LinearLayout = findViewById(R.id.linlay_1)
        val layout1: LinearLayout = findViewById(R.id.linlay_2)
        val layout2: LinearLayout = findViewById(R.id.linlay_3)
        val layout3: LinearLayout = findViewById(R.id.linlay_4)
        val layout4: LinearLayout = findViewById(R.id.linlay_5)
        val layout5: LinearLayout = findViewById(R.id.linlay_6)
        var params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            1.0f
        )


        db.collection("inv")
            .get()
            .addOnSuccessListener { result ->
                Log.d(TAG,"${result.size()}")
                var counter = 0
                var layout_path_img = layout
                var layout_path_names = layout1
                for (document in result) {
                    counter++
                    if (counter > 3) {
                        layout_path_img = layout2
                        layout_path_names = layout3
                        if (counter > 6) {
                            layout_path_img = layout4
                            layout_path_names = layout5
                        }
                    }
                    Log.d(TAG, "${document.id} => ${document.data}")
                    Log.d(TAG,"${document.data.get("name")}")
                    val imageView = ImageView(this)
                    Picasso.get().load("${document.data.get("img")}").into(imageView)
                    imageView.adjustViewBounds = true
                    imageView.setLayoutParams(params)
                    imageView.setOnClickListener(){
                        val intent = Intent(
                            applicationContext,
                            MainActivity2::class.java
                        )
                        intent.putExtra("inv_id", "${document.id}")
                        startActivity(intent)
                    }
                    layout_path_img.addView(imageView)
                    val textView = TextView(this)
                    textView.text = "${document.data.get("name")}"
                    textView.textSize = 15f
                    textView.setLayoutParams(params)
                    layout_path_names.addView(textView)

                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }

    }


}