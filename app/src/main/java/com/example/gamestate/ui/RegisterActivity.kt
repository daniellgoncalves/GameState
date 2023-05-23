package com.example.gamestate.ui

import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.gamestate.R
import com.example.gamestate.ui.data.User
import com.example.gamestate.ui.data.UserViewModel
import com.google.android.material.internal.ContextUtils.getActivity
import java.util.Collections
import java.util.Locale


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
        val confpassword : EditText = findViewById(R.id.register_editconfpassword)
        val country : Spinner = findViewById(R.id.register_editcountry)

        val locales = Locale.getAvailableLocales()
        val countries = ArrayList<String>()
        for (locale in locales) {
            val country = locale.displayCountry
            if (country.trim { it <= ' ' }.length > 0 && !countries.contains(country)) {
                countries.add(country)
            }
        }

        Collections.sort(countries)
        for (country in countries) {
            println(country)
        }

        val countryAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item, countries
        )

        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the your spinner
        // Apply the adapter to the your spinner
        country.setAdapter(countryAdapter)

        fun inputCheck(username: String, email: String, password: String): Boolean {
            return !(TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
        }

        fun insertIntoDatabase() {
            val usernametext = username.text.toString()
            val emailtext = email.text.toString()
            val passwordtext = password.text.toString()
            val confpasswordtext = confpassword.text.toString()

            if(inputCheck(usernametext, emailtext, passwordtext)){
                if(confpasswordtext == passwordtext) {
                    // Criar user
                    val user = User(0, usernametext, emailtext, passwordtext)
                    // Meter user na db
                    mUserViewModel.addUser(user)
                    Toast.makeText(this, "User created!", Toast.LENGTH_SHORT).show()
                    // Voltar
                    finish()
                } else {
                    Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegister.setOnClickListener {
            insertIntoDatabase()
        }
    }
}