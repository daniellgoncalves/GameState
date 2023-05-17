package com.example.gamestate.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.gamestate.R
import com.example.gamestate.ui.data.User
import com.example.gamestate.ui.data.UserViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var mUserViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val btnRegister : Button = findViewById(R.id.register_button)
        val username : EditText = findViewById(R.id.register_editusername)
        val email : EditText = findViewById(R.id.register_editemail)
        val password : EditText = findViewById(R.id.register_editpassword)
        // val country : Spinner = findViewById(R.id.register_editcountry)

        fun inputCheck(username: String, email: String, password: String): Boolean {
            return !(TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
        }

        fun insertIntoDatabase() {
            val usernameText = username.text.toString()
            val emailText = email.text.toString()
            val passwordText = password.text.toString()

            if(inputCheck(usernameText, emailText, passwordText)){
                // Criar user
                val user = User(0, usernameText, emailText, passwordText)
                // Meter user na db
                mUserViewModel.addUser(user)
                Toast.makeText(this, "User created", Toast.LENGTH_SHORT).show()
                // Voltar
                finish()
            } else {
                Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegister.setOnClickListener {
            insertIntoDatabase()
        }
    }
}