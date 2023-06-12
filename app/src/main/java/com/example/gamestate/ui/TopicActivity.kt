package com.example.gamestate.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
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
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.gamestate.ui.data.FragmentTopic
interface RecyclerViewUpdateListener {
    fun updateRecyclerView(dataList: ArrayList<Comment>)
}
class TopicActivity : AppCompatActivity(), RecyclerViewUpdateListener {
    private var settings = arrayOf("Settings","Logout")
    private var images = intArrayOf(R.drawable.baseline_settings_24,R.drawable.baseline_logout_24)

    private lateinit var adapter: RecViewTopicAdapter
    private  var commentsList = ArrayList<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic)

        val server_ip = resources.getString(R.string.server_ip)

        val gameID = intent.getIntExtra("forum_id",-1)
        val topicID = intent.getStringExtra("id")

        var likes = findViewById<TextView>(R.id.number_likes)
        var dislikes = findViewById<TextView>(R.id.number_dislikes)

        // Retornar à página caso username esteja guardado
        val username: TextView = findViewById(R.id.homePage_user_text)
        val topicCommentButton: Button = findViewById(R.id.topic_comment_button)
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
        val adapter1 = SpinnerAdapter(applicationContext, images, settings)
        spin.adapter = adapter1

        val recyclerView = findViewById<RecyclerView>(R.id.topic_recyclerview)

        val linearLayoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        topicCommentButton.setOnClickListener {
            val fragment = FragmentTopic()
            val args = Bundle()
            args.putString("topicid", topicID)
            args.putString("username", username.text.toString())
            args.putSerializable("arraylistcomment",commentsList)
            fragment.arguments = args
            val fragmentManager: FragmentManager = supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainer, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        if (gameID == -1) {
            Toast.makeText(applicationContext, "Game ID missing", Toast.LENGTH_SHORT).show()
        } else {

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

                                likes.text = responseJson.getJSONObject("message").getJSONObject("topics").getString("likes")
                                dislikes.text = responseJson.getJSONObject("message").getJSONObject("topics").getString("dislikes")

                                val title: TextView = findViewById(R.id.topicTitle_tv)
                                val text: TextView = findViewById(R.id.topicText_tv)

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
                                    adapter = RecViewTopicAdapter(commentsList)
                                    recyclerView.adapter = adapter
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
            var likeStatus: Number = 0
            var dislikeStatus: Number = 0

            val retrofitLikeDislike = Retrofit.Builder()
                .baseUrl(server_ip)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val serviceLikeDislike = retrofitLikeDislike.create(RetroFitService::class.java)

            likeButton.setOnClickListener {
                Log.d("lol", likes.toString())
                var number_likes = likes.text.toString().toInt()
                var number_dislikes = dislikes.text.toString().toInt()

                if(likeStatus == 0 && dislikeStatus == 0) {
                    likeButton.setBackgroundColor(Color.parseColor("#6624FF00"))
                    dislikeButton.setBackgroundColor(Color.parseColor("#33FF5151"))
                    number_likes += 1
                    likeStatus = 1
                } else if (likeStatus == 1 && dislikeStatus == 0) {
                    likeButton.setBackgroundColor(Color.parseColor("#3324FF00"))
                    dislikeButton.setBackgroundColor(Color.parseColor("#33FF5151"))
                    number_likes -= 1
                    likeStatus = 0
                } else if (likeStatus == 0 && dislikeStatus == 1) {
                    likeButton.setBackgroundColor(Color.parseColor("#6624FF00"))
                    dislikeButton.setBackgroundColor(Color.parseColor("#33FF5151"))
                    number_likes += 1
                    number_dislikes -= 1
                    likeStatus = 1
                    dislikeStatus = 0
                }
                likes.text = number_likes.toString()
                dislikes.text = number_dislikes.toString()

                val requestBodyLikeDislike = JsonObject()
                requestBodyLikeDislike.addProperty("topic_id", topicID)
                requestBodyLikeDislike.addProperty("likes", number_likes)
                requestBodyLikeDislike.addProperty("dislikes", number_dislikes)

                val callLikeDislike = serviceLikeDislike.likeDislikeTopic(requestBodyLikeDislike)

                val r = Runnable {
                    callLikeDislike.enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            callID: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            if (response.isSuccessful) {
                                val res = response.body()?.string()
                                val responseJson = JSONObject(res!!)
                                if (responseJson.getInt("status") == 200) {

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

            }

            dislikeButton.setOnClickListener {
                var number_likes = likes.text.toString().toInt()
                var number_dislikes = dislikes.text.toString().toInt()
                if(likeStatus == 0 && dislikeStatus == 0) {
                    likeButton.setBackgroundColor(Color.parseColor("#3324FF00"))
                    dislikeButton.setBackgroundColor(Color.parseColor("#66FF5151"))
                    number_dislikes += 1
                    dislikeStatus = 1
                } else if (likeStatus == 0 && dislikeStatus == 1) {
                    likeButton.setBackgroundColor(Color.parseColor("#3324FF00"))
                    dislikeButton.setBackgroundColor(Color.parseColor("#33FF5151"))
                    number_dislikes -= 1
                    dislikeStatus = 0
                } else if (likeStatus == 1 && dislikeStatus == 0) {
                    likeButton.setBackgroundColor(Color.parseColor("#3324FF00"))
                    dislikeButton.setBackgroundColor(Color.parseColor("#66FF5151"))
                    number_dislikes += 1
                    number_likes -= 1
                    dislikeStatus = 1
                    likeStatus = 0
                }
                likes.text = number_likes.toString()
                dislikes.text = number_dislikes.toString()

                val requestBodyLikeDislike = JsonObject()
                requestBodyLikeDislike.addProperty("topic_id", topicID)
                requestBodyLikeDislike.addProperty("likes", number_likes)
                requestBodyLikeDislike.addProperty("dislikes", number_dislikes)

                val callLikeDislike = serviceLikeDislike.likeDislikeTopic(requestBodyLikeDislike)

                val r = Runnable {
                    callLikeDislike.enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            callID: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            if (response.isSuccessful) {
                                val res = response.body()?.string()
                                val responseJson = JSONObject(res!!)
                                if (responseJson.getInt("status") == 200) {

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

            }
        }
    }

    override fun updateRecyclerView(dataList: ArrayList<Comment>) {
        // Update the RecyclerView in the MainActivity
        adapter.setData(dataList)
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