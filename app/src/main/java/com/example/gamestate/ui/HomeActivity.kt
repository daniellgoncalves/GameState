package com.example.gamestate.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestate.R
import com.example.gamestate.ui.data.Home.RecViewHomeAdapter
import com.example.gamestate.ui.data.Home.SpinnerAdapter
import com.example.gamestate.ui.data.RetroFitService
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeActivity : AppCompatActivity() {

    private var settings = arrayOf("Settings","Logout")
    private var images = intArrayOf(R.drawable.baseline_settings_24,R.drawable.baseline_logout_24)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val username: TextView = findViewById(R.id.home_user_text)
        val spin: Spinner = findViewById(R.id.home_header_spinner)
        val sharedPreferences = application.getSharedPreferences("login", Context.MODE_PRIVATE)
        val loginAutomatic = sharedPreferences.getString("username","")
        val searchgametext : EditText = findViewById(R.id.home_search_edit_text)
        val recyclerView = findViewById<RecyclerView>(R.id.home_games_recyclerview)
        username.setText(loginAutomatic)

        fun searhgame() {
            val nameText = searchgametext.text.toString()
            val server_ip = resources.getString(R.string.server_ip)
            val retrofit = Retrofit.Builder()
                .baseUrl(server_ip)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val userService = retrofit.create(RetroFitService::class.java)
            val requestBody = JsonObject()
            requestBody.addProperty("name", nameText)

            val call = userService.sendgame(requestBody)
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
                                recyclerView.adapter = RecViewHomeAdapter(listOf(msm), ContextCompat.getColor(applicationContext, R.color.gold20))
                                recyclerView.layoutManager = LinearLayoutManager(applicationContext)
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

        searchgametext.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                searhgame()

            }

        })

        spin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if(position == 1){
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putString("username","")
                    editor.apply()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        val forumAdapter = SpinnerAdapter(applicationContext, images, settings)
        spin.adapter = forumAdapter
    }
}





