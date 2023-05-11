package com.example.gamestate.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.gamestate.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn_Login: Button = findViewById(R.id.main_login_button)
        val newaccountinfo : TextView = findViewById(R.id.main_newaccountinfo)
        val forgotpasswordinfo: TextView = findViewById(R.id.main_forgotpassword)
        //val editpassword: EditText = findViewById(R.id.main_editpassword)
        //val editusername: EditText = findViewById(R.id.main_editusername)

        newaccountinfo.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }
        forgotpasswordinfo.setOnClickListener {
            startActivity(Intent(this,ForgotPasswordActivity::class.java))
        }
        btn_Login.setOnClickListener {
            startActivity(Intent(this,Home_Activity::class.java))
        }
    }
}