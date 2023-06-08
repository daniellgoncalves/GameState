package com.example.gamestate.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ImageButton
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
import com.example.gamestate.ui.data.Home.SpinnerAdapter
import com.example.gamestate.ui.data.RetroFitService
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONObject
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale

class TopicActivity : AppCompatActivity() {
    private var settings = arrayOf("Settings","Logout")
    private var images = intArrayOf(R.drawable.baseline_settings_24,R.drawable.baseline_logout_24)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic)

        // Retornar à página caso username esteja guardado
        val username: TextView = findViewById(R.id.home_user_text)
        val sharedPreferences = application.getSharedPreferences("login", Context.MODE_PRIVATE)
        val loginAutomatic = sharedPreferences.getString("username","")
        username.text = loginAutomatic

        val spin: Spinner = findViewById(R.id.home_header_spinner)

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
        val adapter = SpinnerAdapter(applicationContext, images, settings)
        spin.adapter = adapter

        // Retirar o id do jogo e do tópico
        val gameID = intent.getIntExtra("forum_id",-1)
        val topicID = intent.getStringExtra("id")

        if (gameID == -1) {
            Toast.makeText(applicationContext, "Game ID missing", Toast.LENGTH_SHORT).show()
        } else {
            val server_ip = resources.getString(R.string.server_ip)

            val retrofit = Retrofit.Builder()
                .baseUrl(server_ip)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(RetroFitService::class.java)

            val requestBodyGame = JsonObject()
            requestBodyGame.addProperty("id", gameID)

            val requestBodyTopic = JsonObject()
            requestBodyTopic.addProperty("topic_id", topicID)

            val callGame = service.sendGameByID(requestBodyGame)
            val callID = service.sendTopicByID(requestBodyTopic)

            val r = Runnable {
                callGame.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        callGame: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            val res = response.body()?.string()
                            val responseJson = JSONObject(res!!)
                            if (responseJson.getInt("status") == 200) {
                                val name: TextView = findViewById(R.id.gameName)
                                val developer: TextView = findViewById(R.id.gameCompany)
                                val releaseDate: TextView = findViewById(R.id.gameDate)
                                val gameImage: ImageView = findViewById(R.id.gameImage)
                                val developerImage: ImageView = findViewById(R.id.gameCompanyImage)

                                val date =
                                    responseJson.getJSONObject("message").getString("release_date")

                                val inputFormat =
                                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val outputFormat =
                                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                                val parsedDate = inputFormat.parse(date)
                                val formattedDate = outputFormat.format(parsedDate)

                                name.text = responseJson.getJSONObject("message").getString("name")
                                developer.text =
                                    responseJson.getJSONObject("message").getJSONArray("developers")
                                        .getJSONObject(0).getString("name")
                                releaseDate.text = formattedDate
                                val gameImageUrl =
                                    responseJson.getJSONObject("message").getString("image")
                                val developerImageUrl =
                                    responseJson.getJSONObject("message").getJSONArray("developers")
                                        .getJSONObject(0).getString("image")

                                Glide.with(applicationContext)
                                    .load(gameImageUrl)
                                    .centerCrop()
                                    .into(gameImage)

                                Glide.with(applicationContext)
                                    .load(developerImageUrl)
                                    .centerCrop()
                                    .into(developerImage)
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    responseJson.getString("message"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(applicationContext, "Network Failure", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
            }
            val t = Thread(r)
            t.start()

            val r_topic = Runnable {
                callID.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        callID: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            val res = response.body()?.string()
                            val responseJson = JSONObject(res!!)
                            if (responseJson.getInt("status") == 200) {
                                val title: TextView = findViewById(R.id.topicTitle)
                                val text: TextView = findViewById(R.id.topicText_tv)

                                Log.d("lol_name", responseJson.getJSONObject("message").getJSONObject("topics").getString("name"))
                                Log.d("lol_text", responseJson.getJSONObject("message").getJSONObject("topics").getString("text"))

                                title.text = responseJson.getJSONObject("message").getJSONObject("topics").getString("name")
                                text.text = responseJson.getJSONObject("message").getJSONObject("topics").getString("text")

                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    responseJson.getString("message"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(applicationContext, "Network Failure", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
            }
            val t_topic = Thread(r_topic)
            t_topic.start()

            val likeButton = findViewById<ImageButton>(R.id.topic_like_button)
            val dislikeButton = findViewById<ImageButton>(R.id.topic_dislike_button)

            likeButton.setOnClickListener {
                likeButton.alpha = 0.7F
                dislikeButton.alpha = 0.5F
            }

            dislikeButton.setOnClickListener {
                dislikeButton.alpha = 0.7F
                likeButton.alpha = 0.5F
            }

            val recyclerView = findViewById<RecyclerView>(R.id.topic_recyclerview)
            recyclerView.adapter = RecViewForumAdapter(listOf("Comentario Kazzio",
                "Comentario Ric",
                "Comentario User"), ContextCompat.getColor(applicationContext, R.color.gold20))
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
    }
}