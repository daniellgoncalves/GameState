package com.example.gamestate.ui

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
import com.example.gamestate.ui.data.RetroFitService
import com.example.gamestate.ui.data.UserViewModel
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    private  lateinit var sharedPreferences: SharedPreferences
    private lateinit var mUserViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val btnLogin: Button = findViewById(R.id.main_login_button)
        val newAccountInfo : TextView = findViewById(R.id.main_newaccountinfo)
        val forgotPasswordInfo: TextView = findViewById(R.id.main_forgotpassword)
        val editPassword: EditText = findViewById(R.id.main_editpassword)
        val editUsername: EditText = findViewById(R.id.main_editusername)

        val server_ip = resources.getString(R.string.server_ip)

        sharedPreferences = application.getSharedPreferences("login", Context.MODE_PRIVATE)
        val loginAutomatic = sharedPreferences.getString("username","")
        if (loginAutomatic != null) {
            if (loginAutomatic.isNotEmpty()) {
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }

        newAccountInfo.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }

        forgotPasswordInfo.setOnClickListener {
            startActivity(Intent(this,ForgotPasswordActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val username = editUsername.text.toString()
            val password = editPassword.text.toString()
            if (username.isNotEmpty() &&  password.isNotEmpty())
            {
                //val loginStatus = mUserViewModel.LoginUser(username, password)

                val retrofit = Retrofit.Builder()
                    .baseUrl(server_ip)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val service = retrofit.create(RetroFitService::class.java)
                val requestBody = JsonObject()
                requestBody.addProperty("username", username)
                requestBody.addProperty("password", password)
                val call = service.login(requestBody)
                val r = Runnable {
                    call.enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful) {
                                val res = response.body()?.string()
                                val responseJson = JSONObject(res!!)
                                val status = responseJson.getInt("status")
                                val msm = responseJson.getString("message")
                                if (status == 200)
                                {
                                    val editor:SharedPreferences.Editor = sharedPreferences.edit()
                                    editor.putString("username",username)
                                    editor.apply()
                                    Toast.makeText(applicationContext, msm, Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                                    finish()
                                }
                                else {
                                    Toast.makeText(applicationContext, msm, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Toast.makeText(applicationContext, "Network Failure", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                val t = Thread(r)
                t.start()
               /* if(loginStatus == 1)
                {
                    val editor:SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putString("username",username)
                    editor.apply()
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,HomeActivity::class.java))
                }
                else if(loginStatus == -1)
                {
                    Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
                }*/
            }
            else
            {
                Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}