package com.example.gamestate.ui

import android.R.attr.rating
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.RatingBar.OnRatingBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
import java.util.Locale


class AddGameActivity : AppCompatActivity() {
    private var settings = arrayOf("Settings","Logout")
    private var images = intArrayOf(R.drawable.baseline_settings_24,R.drawable.baseline_logout_24)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_game)

        val username: TextView = findViewById(R.id.homePage_user_text)
        val sharedPreferences = application.getSharedPreferences("login", MODE_PRIVATE)
        val loginAutomatic = sharedPreferences.getString("username","")
        val userid = sharedPreferences.getString("userid","")
        val token = sharedPreferences.getString("token","")
        username.text = loginAutomatic

        username.setOnClickListener {
            val intent = Intent(this,ProfileActivity::class.java)
            startActivity(intent)
        }

        val spin: Spinner = findViewById(R.id.home_header_spinner)
        val reviewButton = findViewById<Button>(R.id.makeReview_button)

        val ratingBar = findViewById<RatingBar>(R.id.rating)

        val title = findViewById<EditText>(R.id.reviewTitle_et).text
        val text = findViewById<EditText>(R.id.reviewText_et).text

        val library: ImageButton = findViewById(R.id.homePage_library)
        val notificationbutton: ImageButton = findViewById(R.id.homePage_notifications)
        val homeButton: ImageButton = findViewById(R.id.home_home)

        var userPicture: ImageView = findViewById(R.id.homePage_user)

        val imageUriString = sharedPreferences.getString("imageUri", null)

        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)


            // Use the retrieved image URI
            Glide.with(this)
                .load(imageUri)
                .apply(
                    RequestOptions()
                    .centerCrop()
                    .override(100, 100)) // Specify the desired dimensions of the ImageView
                .into(userPicture)
        }

        homeButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        library.setOnClickListener {
            startActivity(Intent(this, LibraryActivity::class.java))
        }
        notificationbutton.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
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

        val gameID = intent.getIntExtra("id",-1)
        if (gameID == -1) {
            Toast.makeText(applicationContext, "Game ID missing", Toast.LENGTH_SHORT).show()
        } else {
            val server_ip = resources.getString(R.string.server_ip)

            val retrofit = Retrofit.Builder()
                .baseUrl(server_ip)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(RetroFitService::class.java)

            val requestBody = JsonObject()
            requestBody.addProperty("id", gameID)

            val call = service.searchByID(token!!, gameID)

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
                                val gameImage: ImageView = findViewById(R.id.selectedGame_iv)
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

            val finishButton = findViewById<LinearLayout>(R.id.gameStatusFinished_button)
            val stillPlayingButton = findViewById<LinearLayout>(R.id.gameStatusStillPlaying_button)
            val pauseButton = findViewById<LinearLayout>(R.id.gameStatusPaused_button)
            val quitButton = findViewById<LinearLayout>(R.id.gameStatusStopped_button)
            var buttonState = 0;


            finishButton.setOnClickListener {
                finishButton.setBackgroundColor(Color.parseColor("#6624FF00"))
                stillPlayingButton.setBackgroundColor(Color.parseColor("#33FBFF4C"))
                pauseButton.setBackgroundColor(Color.parseColor("#33FFA840"))
                quitButton.setBackgroundColor(Color.parseColor("#33FF5151"))
                buttonState = 1;
            }

            stillPlayingButton.setOnClickListener {
                stillPlayingButton.setBackgroundColor(Color.parseColor("#66FBFF4C"))
                finishButton.setBackgroundColor(Color.parseColor("#3324FF00"))
                pauseButton.setBackgroundColor(Color.parseColor("#33FFA840"))
                quitButton.setBackgroundColor(Color.parseColor("#33FF5151"))
                buttonState = 2;
            }

            pauseButton.setOnClickListener {
                pauseButton.setBackgroundColor(Color.parseColor("#66FFA840"))
                finishButton.setBackgroundColor(Color.parseColor("#3324FF00"))
                stillPlayingButton.setBackgroundColor(Color.parseColor("#33FBFF4C"))
                quitButton.setBackgroundColor(Color.parseColor("#33FF5151"))
                buttonState = 3;
            }

            quitButton.setOnClickListener {
                quitButton.setBackgroundColor(Color.parseColor("#66FF5151"))
                finishButton.setBackgroundColor(Color.parseColor("#3324FF00"))
                stillPlayingButton.setBackgroundColor(Color.parseColor("#33FBFF4C"))
                pauseButton.setBackgroundColor(Color.parseColor("#33FFA840"))
                buttonState = 4;
            }

            reviewButton.setOnClickListener {

                val getrating = ratingBar.getRating()

                val retrofit = Retrofit.Builder()
                    .baseUrl(server_ip)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val service = retrofit.create(RetroFitService::class.java)

                val requestBody = JsonObject()
                requestBody.addProperty("rating", getrating)
                requestBody.addProperty("title", title.toString())
                requestBody.addProperty("text", text.toString())
                requestBody.addProperty("gameStatus", buttonState)
                requestBody.addProperty("user_id", userid)
                requestBody.addProperty("forum_id", gameID)

                Log.d("rating", getrating.toString())

                val call = service.createReview(token!!, requestBody)

                val r = Runnable {
                    call.enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            callID: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            if (response.isSuccessful) {
                                val res = response.body()?.string()
                                val responseJson = JSONObject(res!!)
                                if (responseJson.getInt("status") == 200) {
                                    Toast.makeText(
                                        applicationContext,
                                        responseJson.getString("message"),
                                        Toast.LENGTH_SHORT
                                    ).show()
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
                            Toast.makeText(
                                applicationContext,
                                "Network Failure",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    })
                }
                val t = Thread(r)
                t.start()

                val intent = Intent(this,GameActivity::class.java)
                intent.putExtra("id",gameID);
                startActivity(intent)
            }
        }
    }
}