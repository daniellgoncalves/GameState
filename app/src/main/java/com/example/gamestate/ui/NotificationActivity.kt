package com.example.gamestate.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestate.R
import com.example.gamestate.ui.data.Home.SpinnerAdapter
import com.example.gamestate.ui.data.Notification.RecViewNotificationAdapter
import com.example.gamestate.ui.data.RetroFitService
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotificationActivity : AppCompatActivity() {
    private var settings = arrayOf("Settings","Logout")
    private var images = intArrayOf(R.drawable.baseline_settings_24,R.drawable.baseline_logout_24)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        val spinnerHeader: Spinner = findViewById(R.id.home_header_spinner)
        val username: TextView = findViewById(R.id.homePage_user_text)
        //val notificationstv: TextView = findViewById(R.id.notifications_tv)
        val sharedPreferences = application.getSharedPreferences("login", Context.MODE_PRIVATE)
        val loginAutomatic = sharedPreferences.getString("username","")
        val token = sharedPreferences.getString("token","")

        val library: ImageButton = findViewById(R.id.homePage_library)
        val homeButton: ImageButton = findViewById(R.id.home_home)

        homeButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        library.setOnClickListener {
            startActivity(Intent(this, LibraryActivity::class.java))
        }

        val linearLayoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        username.text = loginAutomatic
        spinnerHeader.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position == 1) {
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putString("username", "")
                    editor.apply()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        val adapter = SpinnerAdapter(applicationContext, images, settings)
        spinnerHeader.adapter = adapter
        val recyclerView = findViewById<RecyclerView>(R.id.notifications_recyclerview)
        val serverIP = resources.getString(R.string.server_ip)

        val retrofit = Retrofit.Builder()
            .baseUrl(serverIP)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(RetroFitService::class.java)

        val requestBodyUser = JsonObject()
        requestBodyUser.addProperty("username", loginAutomatic)

        val callUser = service.sendTopicByUser(token!!, loginAutomatic!!)

        val mainHandler = Handler(Looper.getMainLooper())

        val r = Runnable {
            callUser.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(callUser: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val res = response.body()?.string()
                        val responseJson = JSONObject(res!!)
                        if (responseJson.getInt("status") == 200) {
                            var isoDate: String
                            val commentsbytopicsArray = responseJson.getJSONObject("message").getJSONArray("commentsbytopicos")
                            val topicIDListcomment = ArrayList<String>()
                            val topicImageList = ArrayList<String>()
                            val usernameList = ArrayList<String>()
                            val forumID = ArrayList<Int>()
                            val elapsedtimeList = ArrayList<String>()
                            for (i in 0 until commentsbytopicsArray.length()) {
                                    topicIDListcomment.add(commentsbytopicsArray.getJSONObject(i).getString("topicid"))
                                    forumID.add(commentsbytopicsArray.getJSONObject(i).getInt("gameid"))
                                    topicImageList.add(commentsbytopicsArray.getJSONObject(i).getString("image"))
                                    usernameList.add(commentsbytopicsArray.getJSONObject(i).getString("username") + " commented on your topic")
                                    isoDate = commentsbytopicsArray.getJSONObject(i).getString("createdAt")
                                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                                    dateFormat.timeZone = TimeZone.getTimeZone("GMT")
                                    val parsedDate = dateFormat.parse(isoDate)
                                    val currentTime = System.currentTimeMillis()
                                    val elapsedTimeMillis = currentTime - parsedDate.time
                                    val elapsedTime = formatDuration(elapsedTimeMillis)
                                    elapsedtimeList.add(elapsedTime)
                                }
                               mainHandler.post {
                                          recyclerView.adapter = RecViewNotificationAdapter(usernameList,elapsedtimeList,topicImageList, topicIDListcomment,forumID)
                                          recyclerView.layoutManager = linearLayoutManager
                                       }

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