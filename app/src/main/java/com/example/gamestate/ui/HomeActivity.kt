package com.example.gamestate.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import com.example.gamestate.R
import com.example.gamestate.ui.data.CustomAdapter

class HomeActivity : AppCompatActivity() {

    private var settings = arrayOf("Settings","Logout")
    private var images = intArrayOf(R.drawable.baseline_settings_24,R.drawable.baseline_logout_24)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // val button : ImageButton = findViewById(R.id.user_settings)
        val username: TextView = findViewById(R.id.home_user_text)
        val spin: Spinner = findViewById(R.id.home_header_spinner)
        val sharedPreferences = application.getSharedPreferences("login", Context.MODE_PRIVATE)
        val loginAutomatic = sharedPreferences.getString("username","")

        username.setText(loginAutomatic)

        spin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if(position == 1){
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putString("username","")
                    editor.apply()
                    finish()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        val customAdapter = CustomAdapter(applicationContext, images, settings)
        spin.adapter = customAdapter
    }
}


