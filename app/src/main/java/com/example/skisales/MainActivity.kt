package com.example.skisales

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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

        val db = Firebase.firestore
        val main_parent_vert_layout: LinearLayout = findViewById(R.id.linlayvert)
        var params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            1.0f
        )
        var params_ll = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )



        db.collection("inv")
            .get()
            .addOnSuccessListener { result ->
                Log.d(TAG,"${result.size()}")
                var counter = 0
                var layout_path_img = main_parent_vert_layout
                var layout_path_names = main_parent_vert_layout
                for (document in result) {
                    if (counter == 0 || counter % 3 == 0) {

                        val layout_new_parent_vert = LinearLayout(this)
                        layout_new_parent_vert.layoutParams = params_ll
                        layout_new_parent_vert.orientation = LinearLayout.VERTICAL
                        main_parent_vert_layout.addView(layout_new_parent_vert)

                        val layout_new_hor_images = LinearLayout(this)
                        layout_new_hor_images.layoutParams = params_ll
                        layout_new_hor_images.orientation = LinearLayout.HORIZONTAL
                        layout_new_parent_vert.addView(layout_new_hor_images)

                        val layout_new_hor_names = LinearLayout(this)
                        layout_new_hor_names.layoutParams = params_ll
                        layout_new_hor_names.orientation = LinearLayout.HORIZONTAL
                        layout_new_parent_vert.addView(layout_new_hor_names)

                        layout_path_img = layout_new_hor_images
                        layout_path_names = layout_new_hor_names

                    }
                    Log.d(TAG, "${document.id} => ${document.data}") //
                    Log.d(TAG,"${document.data.get("name")}") //

                    val imageView = ImageView(this)
                    Picasso.get().load("${document.data.get("img")}").into(imageView)
                    imageView.maxHeight = 500
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
                    textView.setGravity(Gravity.CENTER);
                    textView.textSize = 15f
                    textView.setLayoutParams(params)
                    layout_path_names.addView(textView)
                    counter++
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception) //
                Toast.makeText(applicationContext, "Произошла ошибка получения данных.", Toast.LENGTH_LONG).show()
            }

    }


}