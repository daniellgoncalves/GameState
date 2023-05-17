package com.example.gamestate.ui

import android.content.Intent
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
import java.io.Serializable

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var mUserViewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val sendEmail: Button = findViewById(R.id.forgot_password_send_button)

        sendEmail.setOnClickListener {
            val email: EditText = findViewById(R.id.forgot_password_editemail)
            val emailText: String = email.text.toString()

            val user: User? = mUserViewModel.getUserByEmail(emailText)

            if(!inputCheck(emailText)) {
                Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
            } else if(user == null) {
                Toast.makeText(this, "Wrong email", Toast.LENGTH_SHORT).show()
            } else {
                //código intermédio de enviar código para email seria aqui

                val intent = Intent(this, ForgotPasswordActivity2::class.java) //no sucesso do código anterior
                intent.putExtra("user", user as Serializable)
                startActivity(intent)
                finish()
            }
        }
    }
    private fun inputCheck(text: String): Boolean {
        return !TextUtils.isEmpty(text)
    }
}