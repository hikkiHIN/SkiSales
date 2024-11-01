package com.example.skisales

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = Firebase.firestore
        var inv_id = ""
        val extras = intent.extras
        if (extras != null) {
            inv_id = extras.getString("inv_id").toString()
        }
        Log.d(TAG, "inv_id = ${inv_id}")


        val spinner: Spinner = findViewById(R.id.spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.core,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        val spinner1: Spinner = findViewById(R.id.spinner1)
        ArrayAdapter.createFromResource(
            this,
            R.array.rigidity,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner1.adapter = adapter
        }

        val spinner2: Spinner = findViewById(R.id.spinner2)
        ArrayAdapter.createFromResource(
            this,
            R.array.sliding_surface,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner2.adapter = adapter
        }

        val spinner3: Spinner = findViewById(R.id.spinner3)
        ArrayAdapter.createFromResource(
            this,
            R.array.notch_system,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner3.adapter = adapter
        }


        val take_order = findViewById<Button>(R.id.button2)
        take_order.setOnClickListener {
                Log.d("BUTTONS", "User tapped the Supabutton")
                val spinner_core = spinner.selectedItem.toString()
                val spinner_rigidity = spinner1.selectedItem.toString()
                val spinner_notch_system = spinner3.selectedItem.toString()
                val spinner_sliding_surface = spinner2.selectedItem.toString()
                val user_weight = findViewById(R.id.editTextNumber2) as EditText
                val order = hashMapOf(
                    "order_skis" to "${inv_id}",
                    "order_user_weight" to user_weight.text.toString().toFloat(),
                    "order_core" to spinner_core,
                    "order_notch_system" to spinner_notch_system,
                    "order_rigidity" to spinner_rigidity,
                    "order_sliding_surface" to spinner_sliding_surface,
                )
            // Add a new document with a generated ID
            db.collection("orders")
                .add(order)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    Toast.makeText(applicationContext, "Заказ успешно отправлен!", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    Toast.makeText(applicationContext, "Произошла ошибка, пожалуйста повторите попытку.", Toast.LENGTH_LONG).show()
                }
            }
    }
}




