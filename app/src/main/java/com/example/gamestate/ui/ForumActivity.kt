package com.example.gamestate.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamestate.R
import com.example.gamestate.ui.data.Forum.RecViewForumAdapter
import com.example.gamestate.ui.data.Forum.SpinnerForumAdapter
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

class ForumActivity : AppCompatActivity() {
    private var settings = arrayOf("Trending topics","New topics","Most liked")
    private var images = intArrayOf(
        R.drawable.baseline_trending_up_24,
        R.drawable.baseline_access_time_24,
        R.drawable.baseline_heart_24)

    private var settings1 = arrayOf("Settings","Logout")
    private var images1 = intArrayOf(R.drawable.baseline_settings_24,R.drawable.baseline_logout_24)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)

        val spinnerHeader: Spinner = findViewById(R.id.home_header_spinner)
        val spinner: Spinner = findViewById(R.id.forum_filter)
        val username: TextView = findViewById(R.id.home_user_text)
        val sharedPreferences = application.getSharedPreferences("login", Context.MODE_PRIVATE)
        val loginAutomatic = sharedPreferences.getString("username","")
        username.text = loginAutomatic

        val gameID = intent.getIntExtra("id",-1)

        if (gameID == -1) {
            Toast.makeText(applicationContext, "Game ID missing", Toast.LENGTH_SHORT).show()
        } else {
            val serverIP = resources.getString(R.string.server_ip)

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
                                val name: TextView = findViewById(R.id.game_text)
                                val developer: TextView = findViewById(R.id.game_infotext)
                                val releaseDate: TextView = findViewById(R.id.game_infotext2)
                                val gameImage: ImageView = findViewById(R.id.second_game)
                                val developerImage: ImageView = findViewById(R.id.game_infoimage)

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

            val customAdapterSettings = SpinnerAdapter(applicationContext, images1, settings1)
            spinnerHeader.adapter = customAdapterSettings

            val customAdapter = SpinnerForumAdapter(applicationContext, images, settings)
            spinner.adapter = customAdapter

            val recyclerView = findViewById<RecyclerView>(R.id.forum_recyclerview)
            recyclerView.adapter = RecViewForumAdapter(listOf("Gosto mais dos antigos",
                "Estou preso nesta quest",
                "Bug no início do jogo",
                "Melhor jogo do ano?",
                "A personagem é muito lenta",
                "Dá para alterar a dificuldade a meio?",
                "O que acharam da história?",
                "Drop de frames quando ataco",
                "Guia para speedrun",
                "O Atreus irrita-me!!!"), ContextCompat.getColor(applicationContext, R.color.gold20))
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
    }
}