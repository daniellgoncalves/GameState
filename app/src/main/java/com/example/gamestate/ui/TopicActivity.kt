package com.example.gamestate.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamestate.R
import com.example.gamestate.ui.data.Home.SpinnerAdapter
import com.example.gamestate.ui.data.RetroFitService
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONObject
import com.example.gamestate.ui.data.Comment
import com.example.gamestate.ui.data.Topic.RecViewTopicAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TopicActivity : AppCompatActivity() {
    private var settings = arrayOf("Settings","Logout")
    private var images = intArrayOf(R.drawable.baseline_settings_24,R.drawable.baseline_logout_24)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic)

        // Retornar à página caso username esteja guardado
        val username: TextView = findViewById(R.id.homePage_user_text)
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

        val recyclerView = findViewById<RecyclerView>(R.id.topic_recyclerview)
        val linearLayoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

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

            val mainHandler = Handler(Looper.getMainLooper())

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
                                val name: TextView = findViewById(R.id.gameName_tv)
                                val developer: TextView = findViewById(R.id.gameCompany_tv)
                                val releaseDate: TextView = findViewById(R.id.gameReleaseDate_tv)
                                val gameImage: ImageView = findViewById(R.id.selectedGame_iv)
                                val developerImage: ImageView = findViewById(R.id.gameCompany_iv)

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
                                val title: TextView = findViewById(R.id.topicTitle_tv)
                                val text: TextView = findViewById(R.id.topicText_tv)

                                val commentsList = ArrayList<Comment>()
                                val commentsArray = responseJson.getJSONObject("message").getJSONObject("topics").getJSONArray("comments")
                                var isoDate: String

                                for(i in 0 until commentsArray.length())
                                {
                                    isoDate = commentsArray.getJSONObject(i).getString("createdAt")

                                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                                    dateFormat.timeZone = TimeZone.getTimeZone("GMT")

                                    val parsedDate = dateFormat.parse(isoDate)

                                    val currentTime = System.currentTimeMillis()
                                    val elapsedTimeMillis = currentTime - parsedDate.time

                                    val elapsedTime = formatDuration(elapsedTimeMillis)

                                    commentsList.add(Comment(
                                        commentsArray.getJSONObject(i).getString("text"),
                                        commentsArray.getJSONObject(i).getString("username"),
                                        elapsedTime)
                                    )
                                }

                                mainHandler.post {
                                    recyclerView.adapter = RecViewTopicAdapter(commentsList)
                                    recyclerView.layoutManager = linearLayoutManager
                                }

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

            val likeButton = findViewById<LinearLayout>(R.id.topic_like_button)
            val dislikeButton = findViewById<LinearLayout>(R.id.topic_dislike_button)
            val likes = findViewById<TextView>(R.id.number_likes)
            val dislikes = findViewById<TextView>(R.id.number_dislikes)
            var number_likes = findViewById<TextView>(R.id.number_likes).text.toString().toInt()
            var number_dislikes = findViewById<TextView>(R.id.number_dislikes).text.toString().toInt()
            var likeStatus = false
            var dislikeStatus = false

            likeButton.setOnClickListener {
                if(likeStatus == false) {
                    likeButton.setBackgroundColor(Color.parseColor("#6624FF00"))
                    dislikeButton.setBackgroundColor(Color.parseColor("#33FF5151"))
                    number_likes += 1
                    if(dislikeStatus == true) {
                        number_dislikes -= 1
                    }
                    likes.text = number_likes.toString()
                    dislikes.text = number_dislikes.toString()
                    likeStatus = true
                    dislikeStatus = false
                } else if (likeStatus == true) {
                    likeButton.setBackgroundColor(Color.parseColor("#3324FF00"))
                    dislikeButton.setBackgroundColor(Color.parseColor("#33FF5151"))
                    number_likes -= 1
                    likes.text = number_likes.toString()
                    likeStatus = false
                    dislikeStatus = false
                }
            }

            dislikeButton.setOnClickListener {
                if(dislikeStatus == false) {
                    dislikeButton.setBackgroundColor(Color.parseColor("#66FF5151"))
                    likeButton.setBackgroundColor(Color.parseColor("#3324FF00"))
                    number_dislikes += 1
                    if(likeStatus == true) {
                        number_likes -= 1
                    }
                    likes.text = number_likes.toString()
                    dislikes.text = number_dislikes.toString()
                    dislikeStatus = true
                    likeStatus = false
                } else if (dislikeStatus == true) {
                    dislikeButton.setBackgroundColor(Color.parseColor("#33FF5151"))
                    likeButton.setBackgroundColor(Color.parseColor("#3324FF00"))
                    number_dislikes -= 1
                    dislikes.text = number_dislikes.toString()
                    dislikeStatus = false
                    likeStatus = false
                }
            }
        }
    }
    fun formatDuration(durationMillis: Long): String {
        val seconds = (durationMillis / 1000) % 60
        val minutes = (durationMillis / (1000 * 60)) % 60
        val hours = (durationMillis / (1000 * 60 * 60)) % 24
        val days = durationMillis / (1000 * 60 * 60 * 24)

        return when {
            days > 0 -> "${days}d"
            hours > 0 -> "${hours}h"
            minutes > 0 -> "${minutes}m"
            seconds > 0 -> "${seconds}s"
            else -> "Just now"
        }
    }
}