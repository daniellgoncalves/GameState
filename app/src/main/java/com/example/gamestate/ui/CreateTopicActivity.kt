package com.example.gamestate.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.example.gamestate.R
import com.example.gamestate.ui.data.Home.SpinnerAdapter
import com.example.gamestate.ui.data.RetroFitService
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class CreateTopicActivity : AppCompatActivity() {
    private var settings = arrayOf("Settings","Logout")
    private var images = intArrayOf(R.drawable.baseline_settings_24,R.drawable.baseline_logout_24)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_topic)

        val username: TextView = findViewById(R.id.homePage_user_text)
        val spin: Spinner = findViewById(R.id.home_header_spinner)
        val sharedPreferences = application.getSharedPreferences("login", Context.MODE_PRIVATE)
        val loginAutomatic = sharedPreferences.getString("username","")
        val userid = sharedPreferences.getString("userid","")
        val title: EditText = findViewById(R.id.createTopic_title_et)
        val topic: EditText = findViewById(R.id.createTopic_text_et)
        val btnTopic : Button = findViewById(R.id.createTopic_button)
        val gameID = intent.getIntExtra("id",0);
        val serverIP = resources.getString(R.string.server_ip)
        username.setText(loginAutomatic)

        val retrofit = Retrofit.Builder()
            .baseUrl(serverIP)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(RetroFitService::class.java)

        val requestBody = JsonObject()
        requestBody.addProperty("id", gameID)

        val call = service.sendGameByID(requestBody)

        val r = Runnable {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val res = response.body()?.string()
                        val responseJson = JSONObject(res!!)
                        if (responseJson.getInt("status") == 200)
                        {
                            val name: TextView = findViewById(R.id.gameName_tv)
                            val developer: TextView = findViewById(R.id.gameCompany_tv)
                            val releaseDate: TextView = findViewById(R.id.gameReleaseDate_tv)
                            val gameImage : ImageView = findViewById(R.id.selectedGame_iv)
                            val developerImage: ImageView = findViewById(R.id.gameCompany_iv)

                            val date = responseJson.getJSONObject("message").getString("release_date")

                            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                            val parsedDate = inputFormat.parse(date)
                            val formattedDate = outputFormat.format(parsedDate)

                            name.text = responseJson.getJSONObject("message").getString("name")
                            developer.text = responseJson.getJSONObject("message").getJSONArray("developers").getJSONObject(0).getString("name")
                            releaseDate.text = formattedDate
                            val gameImageUrl = responseJson.getJSONObject("message").getString("image")
                            val developerImageUrl = responseJson.getJSONObject("message").getJSONArray("developers").getJSONObject(0).getString("image")

                            Glide.with(applicationContext)
                                .load(gameImageUrl)
                                .centerCrop()
                                .into(gameImage)

                            Glide.with(applicationContext)
                                .load(developerImageUrl)
                                .centerCrop()
                                .into(developerImage)
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

        fun topicinit(){
            val titleText = title.text.toString()
            val topicText = topic.text.toString()
            val requestBody1 = JsonObject()
            requestBody1.addProperty("name", titleText)
            requestBody1.addProperty("text", topicText)
            requestBody1.addProperty("user_id",userid)
            requestBody1.addProperty("forum_id",gameID)
            val call1 = service.createTopic(requestBody1)

            val r1 = Runnable {
                call1.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val res = response.body()?.string()
                            val responseJson = JSONObject(res!!)
                            val msm = responseJson.getString("message")
                            if (responseJson.getInt("status") == 200)
                            {
                                Toast.makeText(applicationContext, msm, Toast.LENGTH_SHORT)
                                    .show()
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
            val t1 = Thread(r1)
            t1.start()
        }
        btnTopic.setOnClickListener {
            topicinit()
        }
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
        val Adapter = SpinnerAdapter(applicationContext, images, settings)
        spin.adapter = Adapter

    }
}