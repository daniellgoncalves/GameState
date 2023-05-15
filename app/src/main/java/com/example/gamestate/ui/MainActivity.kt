package com.example.gamestate.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.gamestate.R
import com.example.gamestate.ui.data.UserViewModel

class MainActivity : AppCompatActivity() {
    private  lateinit var sharedPreferences: SharedPreferences
    private lateinit var mUserViewModel: UserViewModel
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        val btn_Login: Button = findViewById(R.id.main_login_button)
        val newaccountinfo : TextView = findViewById(R.id.main_newaccountinfo)
        val forgotpasswordinfo: TextView = findViewById(R.id.main_forgotpassword)
        val editpassword: EditText = findViewById(R.id.main_editpassword)
        val editusername: EditText = findViewById(R.id.main_editusername)

        sharedPreferences = application.getSharedPreferences("login", Context.MODE_PRIVATE)
        val loginautomatic = sharedPreferences.getString("username","")
        if (loginautomatic != null) {
            if (loginautomatic.isNotEmpty()) {
                startActivity(Intent(this, Home_Activity::class.java))
            }
        }

        newaccountinfo.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }
        forgotpasswordinfo.setOnClickListener {
            startActivity(Intent(this,ForgotPasswordActivity::class.java))
        }
        btn_Login.setOnClickListener {
            val username = editusername.text.toString()
            val password = editpassword.text.toString()
            if (username.isNotEmpty() &&  password.isNotEmpty())
            {
                val loginstatus = mUserViewModel.LoginUser(username, password)
                if(loginstatus == 1)
                {
                    val editor:SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putString("username",username)
                    editor.apply()
                    Toast.makeText(this, "Login Correto", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,Home_Activity::class.java))
                }
                else if(loginstatus == -1)
                {
                    Toast.makeText(this, "Login Incorreto", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}