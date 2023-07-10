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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import java.util.concurrent.CompletableFuture


class RegisterActivity : AppCompatActivity() {

    private lateinit var mUserViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // IP do servidor Nest
        val server_ip = resources.getString(R.string.server_ip)

        // Definição de variáveis (elementos XML)
        val btnRegister : Button = findViewById(R.id.register_button)
        val username : EditText = findViewById(R.id.register_username_et)
        val email : EditText = findViewById(R.id.register_email_et)
        val password : EditText = findViewById(R.id.register_password_et)
        val confPassword : EditText = findViewById(R.id.register_confpassword_et)
        val country : Spinner = findViewById(R.id.register_country_spinner)
        val existingAccountInfo : TextView = findViewById(R.id.register_existingaccountinfo_tv)

        val locales = Locale.getAvailableLocales()
        val countries = ArrayList<String>()
        for (locale in locales) {
            val country = locale.displayCountry
            if (country.trim { it <= ' ' }.isNotEmpty() && !countries.contains(country)) {
                countries.add(country)
            }
        }

        countries.sort()
        for (country in countries) {
            println(country)
        }

        val countryAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item, countries
        )

        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        country.adapter = countryAdapter

        fun inputCheck(username: String, email: String, password: String, confPassword: String): Boolean {
            return !(TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confPassword))
        }

        // Criar user
        fun insertIntoDatabase() {
            val usernameText = username.text.toString()
            val emailText = email.text.toString()
            val passwordText = password.text.toString()
            val confPasswordText = confPassword.text.toString()
            val countryText = country.selectedItem.toString()
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
                    getPushToken().thenAccept { token ->
                        requestBody.addProperty("pushToken", token)
                    }

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

        // Funcionalidade de botão para fazer login
        existingAccountInfo.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
    private fun getPushToken(): CompletableFuture<String> {
        val future = CompletableFuture<String>()

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task: Task<String> ->
                if (!task.isSuccessful) {
                    future.completeExceptionally(task.exception!!)
                    return@OnCompleteListener
                }

                val token = task.result
                future.complete(token)
            })

        return future
    }
}
