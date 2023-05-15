package com.example.gamestate.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import com.example.gamestate.R

class Home_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val button : ImageButton = findViewById(R.id.user_settings)
        val username: TextView = findViewById(R.id.myImageViewText)
        val sharedPreferences = application.getSharedPreferences("login", Context.MODE_PRIVATE)
        val loginautomatic = sharedPreferences.getString("username","")
        username.setText(loginautomatic)
        button.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(this, button)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            popupMenu.menuInflater.inflate(R.menu.popupsettings_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_logout ->{
                        editor.putString("username","")
                        editor.apply()
                        finish()
                    }
                }
                true
            })
            popupMenu.show()
        }
    }
}