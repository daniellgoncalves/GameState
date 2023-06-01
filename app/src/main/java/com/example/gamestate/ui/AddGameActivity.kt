package com.example.gamestate.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
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
import java.util.Locale

class AddGameActivity : AppCompatActivity() {
    private var settings1 = arrayOf("Settings","Logout")
    private var images1 = intArrayOf(R.drawable.baseline_settings_24,R.drawable.baseline_logout_24)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_game)

        val spinnerHeader: Spinner = findViewById(R.id.home_header_spinner)
        val username: TextView = findViewById(R.id.home_user_text)
        val sharedPreferences = application.getSharedPreferences("login", Context.MODE_PRIVATE)
        val loginAutomatic = sharedPreferences.getString("username","")
        username.text = loginAutomatic

        val gameID = 904947
        val server_ip = resources.getString(R.string.server_ip)

        val retrofit = Retrofit.Builder()
            .baseUrl(server_ip)
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
                            val name: TextView = findViewById(R.id.gameName)
                            val developer: TextView = findViewById(R.id.gameCompany)
                            val releaseDate: TextView = findViewById(R.id.gameDate)
                            val gameImage: ImageView = findViewById(R.id.gameImage)
                            val developerImage: ImageView = findViewById(R.id.gameCompanyImage)

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

        val finishButton = findViewById<ImageButton>(R.id.gameStatus_finish_btn)
        val stillPlayingButton = findViewById<ImageButton>(R.id.gameStatus_stillPlaying_btn)
        val pauseButton = findViewById<ImageButton>(R.id.gameStatus_paused_btn)
        val quitButton = findViewById<ImageButton>(R.id.gameStatus_stopped_btn)
        var buttonState = 0;


        finishButton.setOnClickListener {
            finishButton.alpha = 0.7F;
            stillPlayingButton.alpha = 0.5F;
            pauseButton.alpha = 0.5F;
            quitButton.alpha = 0.5F;
            buttonState = 1;
        }

        stillPlayingButton.setOnClickListener {
            stillPlayingButton.alpha = 0.7F;
            finishButton.alpha = 0.5F;
            pauseButton.alpha = 0.5F;
            quitButton.alpha = 0.5F;
            buttonState = 2;
        }

        pauseButton.setOnClickListener {
            pauseButton.alpha = 0.7F;
            finishButton.alpha = 0.5F;
            stillPlayingButton.alpha = 0.5F;
            quitButton.alpha = 0.5F;
            buttonState = 3;
        }

        quitButton.setOnClickListener {
            quitButton.alpha = 0.7F;
            finishButton.alpha = 0.5F;
            stillPlayingButton.alpha = 0.5F;
            pauseButton.alpha = 0.5F;
            buttonState = 4;
        }
    }
}