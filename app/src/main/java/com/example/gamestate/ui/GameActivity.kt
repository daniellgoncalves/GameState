package com.example.gamestate.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
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
import java.text.SimpleDateFormat
import java.util.*

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val reviewButton = findViewById<Button>(R.id.review_button)
        val forumButton = findViewById<Button>(R.id.forum_button)

        val gameID = intent.getIntExtra("id",0);
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
                            val gameImage : ImageView = findViewById(R.id.gameimg)
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
        reviewButton.setOnClickListener {
            startActivity(Intent(this, AddGameActivity::class.java))
        }

        forumButton.setOnClickListener {
            startActivity(Intent(this, ForumActivity::class.java))
        }
    }
}