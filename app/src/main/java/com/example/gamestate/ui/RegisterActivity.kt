package com.example.gamestate.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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
        val country : Spinner = findViewById(R.id.register_editcountry)

        fun inputCheck(username: String, email: String, password: String): Boolean {
            return !(TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
        }

        fun insertIntoDatabase() {
            val usernametext = username.text.toString()
            val emailtext = email.text.toString()
            val passwordtext = password.text.toString()

            if(inputCheck(usernametext, emailtext, passwordtext)){
                // Criar user
                val user = User(0, usernametext, emailtext, passwordtext)
                // Meter user na db
                mUserViewModel.addUser(user)
                Toast.makeText(this, "Utilizador criado!", Toast.LENGTH_SHORT).show()
                // Voltar
                finish()
            } else {
                Toast.makeText(this, "Preencha os campos todos.", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegister.setOnClickListener {
            insertIntoDatabase()
        }
    }
}