package com.example.gamestate.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.gamestate.R
import com.example.gamestate.ui.data.RetroFitService
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // IP do servidor Nest
        val server_ip = resources.getString(R.string.server_ip)

        // Definição de variáveis (elementos XML)
        val sendEmail: Button = findViewById(R.id.forgotPW_sendEmail_button)
        val email: EditText = findViewById(R.id.forgotPW_email_et)

        // Enviar email
        sendEmail.setOnClickListener {
            val emailText: String = email.text.toString()
            if(emailText.isEmpty()) {
                Toast.makeText(this, "Type in your email", Toast.LENGTH_SHORT).show()
            } else {
                val retrofit = Retrofit.Builder()
                    .baseUrl(server_ip)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val service = retrofit.create(RetroFitService::class.java)

                val requestBody = JsonObject()
                requestBody.addProperty("email", emailText)

                val call = service.sendEmail(requestBody)

                val r = Runnable {
                    call.enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful) {
                                val res = response.body()?.string()
                                val responseJson = JSONObject(res!!)
                                if (responseJson.getInt("status") == 200)
                                {
                                    Toast.makeText(applicationContext, responseJson.getString("message"), Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(applicationContext, MainActivity::class.java))
                                    finish()
                                }
                                else {
                                    Toast.makeText(applicationContext, responseJson.getString("message"), Toast.LENGTH_SHORT).show()
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
            }
        }
    }
}