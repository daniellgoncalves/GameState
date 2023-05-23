package com.example.gamestate.ui

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestate.R
import com.example.gamestate.ui.data.RetroFitService
import com.example.gamestate.ui.data.User
import com.example.gamestate.ui.data.UserViewModel
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

            //POST('forgotpwd')

            var retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.1.79:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(RetroFitService::class.java)

            val requestBody = JsonObject()
            requestBody.addProperty("email", emailText)

            val call = service.sendEmail(requestBody)

            val r = Runnable {
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call : Call<ResponseBody>, response: Response<ResponseBody>){
                        if(response.code() == 201){
                            val retroFit2 = response.body()?.string()
                            Log.d("lol", retroFit2.toString())
                        }
                    }
                    override fun onFailure(calll: Call<ResponseBody>, t: Throwable){
                        print("error")
                    }
                })
            }

            val t = Thread(r)
            t.start()

            /*val user: User? = mUserViewModel.getUserByEmail(emailText)

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
            }*/
        }
    }
    private fun inputCheck(text: String): Boolean {
        return !TextUtils.isEmpty(text)
    }
}