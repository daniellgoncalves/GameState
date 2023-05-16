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

class ForgotPasswordActivity2 : AppCompatActivity() {

    private lateinit var mUserViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password2)

        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val newPassword: EditText = findViewById(R.id.forgot_password2_editnewpassword)
        val newPasswordRepeat: EditText = findViewById(R.id.forgot_password2_editnewpassword_repeat)
        val resetBtn: Button = findViewById(R.id.forgot_password2_reset_button)

        val oldUser: User = intent.extras?.get("user") as User

        resetBtn.setOnClickListener {
            val newPasswordText: String = newPassword.text.toString()
            val newPasswordRepeatText: String = newPasswordRepeat.text.toString()

            if(!inputCheck(newPasswordText) || !inputCheck(newPasswordRepeatText)) {
                Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
            } else if(newPasswordText != newPasswordRepeatText) {
                Toast.makeText(this, "Passwords must be the same", Toast.LENGTH_SHORT).show()
            } else if(oldUser.password == newPasswordText) {
                Toast.makeText(this, "Can't use old passwords", Toast.LENGTH_SHORT).show()
            } else {
                val user = User(oldUser.id, oldUser.username, oldUser.email, newPasswordText)
                mUserViewModel.updateUser(user)
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
        }
    }
    private fun inputCheck(text: String): Boolean {
        return !TextUtils.isEmpty(text)
    }
}