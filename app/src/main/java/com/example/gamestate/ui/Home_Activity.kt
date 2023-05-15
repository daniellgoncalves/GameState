package com.example.gamestate.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.gamestate.R
import com.example.gamestate.ui.data.CustomAdapter
import java.util.Collections
import java.util.Locale

class Home_Activity : AppCompatActivity() {
    internal var settings = arrayOf("Logout")
    internal var images =
        intArrayOf(R.drawable.baseline_logout_24)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
       // val button : ImageButton = findViewById(R.id.user_settings)
        val username: TextView = findViewById(R.id.myImageViewText)
        val spin: Spinner = findViewById(R.id.editsettingsspinner)
        val sharedPreferences = application.getSharedPreferences("login", Context.MODE_PRIVATE)
        val loginautomatic = sharedPreferences.getString("username","")
        username.setText(loginautomatic)
        spin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Toast.makeText(
                    this@Home_Activity,
                    "You Select Position: " + position + " " + settings[position],
                    Toast.LENGTH_SHORT
                ).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        val customAdapter = CustomAdapter(applicationContext, images, settings)
        spin.adapter = customAdapter
    }
}
       // button.setOnClickListener {
            //val editor: SharedPreferences.Editor = sharedPreferences.edit()
           // editor.putString("username","")
          //  editor.apply()





           // finish()

