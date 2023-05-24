package com.example.gamestate.ui

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.gamestate.R
import com.example.gamestate.ui.data.PostService
import com.example.gamestate.ui.data.User
import com.example.gamestate.ui.data.UserViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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
            val usernameText = username.text.toString()
            val emailText = email.text.toString()
            val passwordText = password.text.toString()
            val countryText = country.toString()
            val user = User(0,usernameText, emailText, passwordText,countryText)
            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.229.82:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val userService = retrofit.create(PostService::class.java)
            val requestBody = JsonObject()
            if(inputCheck(usernameText, emailText, passwordText)){
                requestBody.addProperty("username", user.username)
                requestBody.addProperty("password", user.password)
                requestBody.addProperty("email", user.email)
                requestBody.addProperty("country", user.country)

               //mUserViewModel.addUser(user)

                val call = userService.register(requestBody)
                val r = Runnable {
                    call.enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(call : Call<ResponseBody>, response: Response<ResponseBody>){
                            val res = response.body()?.string()
                            try {
                                val jsonObject = JSONObject(res!!)
                                val status = jsonObject.getInt("status")
                                val msm = jsonObject.getString("message")
                                if(status == 200){
                                    Toast.makeText(applicationContext, msm, Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                else if(status == 203 )
                                {
                                    Toast.makeText(applicationContext, msm, Toast.LENGTH_SHORT).show()
                                }

                            } catch (e: JSONException) {
                                // Handle JSON parsing error
                                // ...
                            }
                        }
                        override fun onFailure(calll: Call<ResponseBody>, t: Throwable){
                            Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                val t = Thread(r)
                t.start()
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

