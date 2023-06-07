package com.example.gamestate.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestate.R
import com.example.gamestate.ui.data.Home.SpinnerAdapter
import com.example.gamestate.ui.data.Library.RecViewLibraryAdapter
import com.example.gamestate.ui.data.RetroFitService
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class LibraryActivity : AppCompatActivity() {

    private var settings = arrayOf("Settings","Logout")
    private var images = intArrayOf(R.drawable.baseline_settings_24,R.drawable.baseline_logout_24)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        val spinnerHeader: Spinner = findViewById(R.id.library_header_spinner)
        val username: TextView = findViewById(R.id.library_user_text)
        val sharedPreferences = application.getSharedPreferences("login", Context.MODE_PRIVATE)
        val loginAutomatic = sharedPreferences.getString("username","")
        username.text = loginAutomatic

        spinnerHeader.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
        spinnerHeader.adapter = adapter

        val recyclerView = findViewById<RecyclerView>(R.id.library_recyclerview)
        val linearLayoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        val serverIP = resources.getString(R.string.server_ip)

        val retrofit = Retrofit.Builder()
            .baseUrl(serverIP)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(RetroFitService::class.java)

        val requestBody = JsonObject()
        requestBody.addProperty("username", loginAutomatic)

        val call = service.sendTopicByUser(requestBody)

        val mainHandler = Handler(Looper.getMainLooper())

        val r = Runnable {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val res = response.body()?.string()
                        val responseJson = JSONObject(res!!)
                        if (responseJson.getInt("status") == 200)
                        {
                            val topicNameList = ArrayList<String>()
                            val topicImageList = ArrayList<String>()
                            val topicsArray = responseJson.getJSONObject("message").getJSONArray("topics")
                            val imagesArray = responseJson.getJSONObject("message").getJSONArray("images")

                            for (i in 0 until topicsArray.length()) {
                                topicNameList.add(topicsArray.getJSONObject(i).getString("name"))
                                topicImageList.add(imagesArray.getString(i))
                            }

                            mainHandler.post {
                                recyclerView.adapter = RecViewLibraryAdapter(topicNameList, topicImageList)
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
}