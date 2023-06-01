package com.example.gamestate.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.gamestate.R
import com.example.gamestate.ui.data.RetroFitService
import com.example.gamestate.ui.data.User
import com.example.gamestate.ui.data.UserViewModel
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Collections
import java.util.Locale


class RegisterActivity : AppCompatActivity() {

    private lateinit var mUserViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val btnRegister : Button = findViewById(R.id.register_button)
        val username : EditText = findViewById(R.id.register_editusername)
        val email : EditText = findViewById(R.id.register_editemail)
        val password : EditText = findViewById(R.id.register_editpassword)
        val confPassword : EditText = findViewById(R.id.register_editconfpassword)
        val country : Spinner = findViewById(R.id.register_editcountry)

        val existingAccountInfo : TextView = findViewById(R.id.register_existingaccountinfo)

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

        fun inputCheck(username: String, email: String, password: String, confPassword: String): Boolean {
            return !(TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confPassword))
        }

        fun insertIntoDatabase() {
            val usernameText = username.text.toString()
            val emailText = email.text.toString()
            val passwordText = password.text.toString()
            val confPasswordText = confPassword.text.toString()
            val countryText = country.selectedItem.toString()
            val server_ip = resources.getString(R.string.server_ip)
            val user = User(0,usernameText, emailText, passwordText,countryText)
            val retrofit = Retrofit.Builder()
                .baseUrl(server_ip)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val userService = retrofit.create(RetroFitService::class.java)
            val requestBody = JsonObject()
            if(inputCheck(usernameText, emailText, passwordText, confPasswordText)) {
                if (confPasswordText == passwordText) {
                    requestBody.addProperty("username", usernameText)
                    requestBody.addProperty("password", passwordText)
                    requestBody.addProperty("email", emailText)
                    requestBody.addProperty("country",countryText)

                  //  mUserViewModel.addUser(user)

                    val call = userService.register(requestBody)
                    val r = Runnable {
                        call.enqueue(object : Callback<ResponseBody> {
                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                val res = response.body()?.string()
                                try {
                                    val jsonObject = JSONObject(res!!)
                                    val status = jsonObject.getInt("status")
                                    val msm = jsonObject.getString("message")
                                    if (status == 200) {
                                        Toast.makeText(applicationContext, msm, Toast.LENGTH_SHORT)
                                            .show()
                                        finish()
                                    } else if (status == 203) {
                                        Toast.makeText(applicationContext, msm, Toast.LENGTH_SHORT)
                                            .show()
                                    }

                                } catch (e: JSONException) {
                                    // Handle JSON parsing error
                                    // ...
                                }
                            }

                            override fun onFailure(calll: Call<ResponseBody>, t: Throwable) {
                                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        })
                    }
                    val t = Thread(r)
                    t.start()
                }
                else
                {
                    Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegister.setOnClickListener {
            insertIntoDatabase()
        }
    }
}