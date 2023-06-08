package com.example.gamestate.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
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
import java.util.regex.Pattern

class GameActivity : AppCompatActivity() {
    private var settings = arrayOf("Settings","Logout")
    private var images = intArrayOf(R.drawable.baseline_settings_24,R.drawable.baseline_logout_24)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val username: TextView = findViewById(R.id.home_user_text)
        val sharedPreferences = application.getSharedPreferences("login", Context.MODE_PRIVATE)
        val loginAutomatic = sharedPreferences.getString("username","")
        username.text = loginAutomatic

        val spin: Spinner = findViewById(R.id.home_header_spinner)
        fun removeBrTags(htmlString: String): String {
            val stringWithoutTags = htmlString.replace("<[^>]+>".toRegex(), "")
            return stringWithoutTags.replace("\n", "")
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
        val adapter = SpinnerAdapter(applicationContext, images, settings)
        spin.adapter = adapter

        val reviewButton = findViewById<Button>(R.id.review_button)
        val forumButton = findViewById<Button>(R.id.forum_button)

        val gameID = intent.getIntExtra("id",0)
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
                            val gamebio: TextView = findViewById(R.id.gamebio_textview)
                            val date = responseJson.getJSONObject("message").getString("release_date")
                            val gamebiotext = responseJson.getJSONObject("message").getString("description")
                            val cleanbr = removeBrTags(gamebiotext)
                            //val cleanString = removeHtmlTags(cleanbr)
                            gamebio.text = cleanbr
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
            val intent = Intent(this, AddGameActivity::class.java)
            intent.putExtra("id", gameID)
            startActivity(intent)
        }

        forumButton.setOnClickListener {
            val intent = Intent(this, ForumActivity::class.java)
            intent.putExtra("id", gameID)
            startActivity(intent)
        }
    }
}