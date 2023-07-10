package com.example.gamestate.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gamestate.R
import com.example.gamestate.ui.data.Game.RecViewGameAdapter
import com.example.gamestate.ui.data.Home.SpinnerAdapter
import com.example.gamestate.ui.data.RetroFitService
import com.example.gamestate.ui.data.ReviewsGameFragment
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

    // Inicialização das variáveis do spinner de Logout e outras variáveis
    private var settings = arrayOf("Settings","Logout")
    private var images = intArrayOf(R.drawable.baseline_settings_24,R.drawable.baseline_logout_24)
    private var reviewstatus = 0
    private var reviewsnumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // IP do servidor Nest
        val server_ip = resources.getString(R.string.server_ip)

        // Definição de variáveis (elementos XML)
        val recyclerView = findViewById<RecyclerView>(R.id.game_platforms_recyclerview)
        val reviewstitle: TextView = findViewById(R.id.reviews1_tv)
        val reviewsrating: TextView = findViewById(R.id.ratingreview)
        val reviewstitle1: TextView = findViewById(R.id.reviews2)
        val reviewsrating1: TextView = findViewById(R.id.ratingreview2)
        val allgamereviewstv: TextView = findViewById(R.id.allgamereviewstv)
        val imagereviews1: ImageView = findViewById(R.id.imagereviews1)
        val imagereviews2: ImageView = findViewById(R.id.imagereviews2)
        val elipse_point: ImageView = findViewById(R.id.elipse_point)
        val elipse_point2: ImageView = findViewById(R.id.elipse_point2)
        val star: ImageView = findViewById(R.id.star)
        val star2: ImageView = findViewById(R.id.star2)
        val username: TextView = findViewById(R.id.homePage_user_text)
        val library: ImageButton = findViewById(R.id.homePage_library)
        val notificationbutton: ImageButton = findViewById(R.id.homePage_notifications)
        val homeButton: ImageButton = findViewById(R.id.home_home)
        val addWishlistButton: Button = findViewById(R.id.wishlist_button)
        var userPicture: ImageView = findViewById(R.id.homePage_user)
        val spin: Spinner = findViewById(R.id.home_header_spinner)
        val reviewButton = findViewById<Button>(R.id.review_button)
        val forumButton = findViewById<Button>(R.id.forum_button)

        val linearLayoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        // Obtenção de dados sharedPreferences
        val sharedPreferences = application.getSharedPreferences("login", Context.MODE_PRIVATE)
        val loginAutomatic = sharedPreferences.getString("username","")
        val userID = sharedPreferences.getString("userid","")
        val token = sharedPreferences.getString("token","")
        val imageUriString = sharedPreferences.getString("imageUri", null)

        val gameID = intent.getIntExtra("id",0)

        username.text = loginAutomatic

        // Funcionalidade dos botões de header e footer
        username.setOnClickListener {
            val intent = Intent(this,ProfileActivity::class.java)
            startActivity(intent)
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

        // Funcionalidades de outros botões
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

        // Obtenção da imagem de perfil
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

        fun removeBrTags(htmlString: String): String {
            val stringWithoutTags = htmlString.replace("<[^>]+>".toRegex(), "")
            return stringWithoutTags.replace("\n", "")
        }

        // População do spinner de logout
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

        // Obter dados do jogo
        val retrofit = Retrofit.Builder()
            .baseUrl(server_ip)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(RetroFitService::class.java)

        val requestBody = JsonObject()
        requestBody.addProperty("id", gameID)

        val call = service.searchByID(token!!, gameID)

        val mainHandler = Handler(Looper.getMainLooper())

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
                            val gameImage : ImageView = findViewById(R.id.selectedGame_iv)
                            val developerImage: ImageView = findViewById(R.id.gameCompany_iv)
                            val gamebio: TextView = findViewById(R.id.gameBio_tv)
                            val gameRating = findViewById<RatingBar>(R.id.gamePage_ratingBar)
                            val gameRatingNumber = findViewById<TextView>(R.id.gamePage_ratingValue)
                            val date = responseJson.getJSONObject("message").getString("release_date")
                            val gamebiotext = responseJson.getJSONObject("message").getString("description")
                            val platformList = ArrayList<String>()
                            val platformArray = responseJson.getJSONObject("message").getJSONArray("platforms")
                            val gameRatingValue: String = responseJson.getJSONObject("message").getString("averageRating")
                            if(gameRatingValue != "0") {
                                gameRating.rating = gameRatingValue.toFloat()
                                gameRatingNumber.text = "(" + gameRatingValue + ")"
                            } else {
                                gameRating.rating = 0F
                                gameRatingNumber.text="()"
                            }

                            for (i in 0 until platformArray.length()) {
                                platformList.add(platformArray.getJSONObject(i).getJSONObject("platform").getString("name"))
                            }

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

                            mainHandler.post {
                                recyclerView.adapter = RecViewGameAdapter(platformList)
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

        // Adicionar jogo à wishlist
        fun addToWishlist() {
            val retrofit = Retrofit.Builder()
                .baseUrl(server_ip)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(RetroFitService::class.java)

            val requestBody = JsonObject()
            requestBody.addProperty("game_id", gameID)

            val call = service.addGameToWishlist(token!!, userID!!, requestBody)

            val r = Runnable {
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val res = response.body()?.string()
                            val responseJson = JSONObject(res!!)
                            if (responseJson.getInt("status") == 200)
                            {
                                Toast.makeText(applicationContext, responseJson.getString("message"), Toast.LENGTH_SHORT).show()
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


        // Obter as reviews
        val callReviews = service.searchReviewsbyGameID(token!!,gameID)

        val rReviews = Runnable {
            callReviews.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val res = response.body()?.string()
                        val responseJson = JSONObject(res!!)
                        if (responseJson.getInt("status") == 200)
                        {
                            val reviewsArray = responseJson.getJSONArray("reviewsgame")
                            if(reviewsArray.length() == 1)
                            {
                                reviewstitle.setText(reviewsArray.getJSONObject(0).getString("title"))
                                reviewsrating.setText(reviewsArray.getJSONObject(0).getString("rating"))
                                reviewsrating1.setVisibility(View.GONE)
                                reviewstitle1.setVisibility(View.GONE)
                                imagereviews2.setVisibility(View.GONE)
                                elipse_point2.setVisibility(View.GONE)
                                star2.setVisibility(View.GONE)
                                reviewsnumber = 1
                            }
                            else if(reviewsArray.length() >= 2)
                            {
                                reviewstitle.setText(reviewsArray.getJSONObject(0).getString("title"))
                                reviewsrating.setText(reviewsArray.getJSONObject(0).getString("rating"))
                                reviewstitle1.setText(reviewsArray.getJSONObject(1).getString("title"))
                                reviewsrating1.setText(reviewsArray.getJSONObject(1).getString("rating"))
                                reviewsnumber = 2
                            }

                        }
                        else{
                            reviewsrating.setVisibility(View.GONE)
                            reviewsrating1.setVisibility(View.GONE)
                            reviewstitle.setVisibility(View.GONE)
                            reviewstitle1.setVisibility(View.GONE)
                            imagereviews1.setVisibility(View.GONE)
                            imagereviews2.setVisibility(View.GONE)
                            elipse_point.setVisibility(View.GONE)
                            elipse_point2.setVisibility(View.GONE)
                            star.setVisibility(View.GONE)
                            star2.setVisibility(View.GONE)
                        }
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(applicationContext, "Network Failure", Toast.LENGTH_SHORT).show()
                }
            })
        }
        val tReviews = Thread(rReviews)
        tReviews.start()


        allgamereviewstv.setOnClickListener {
            if ( reviewstatus == 0) {
                reviewsrating.setVisibility(View.GONE)
                reviewsrating1.setVisibility(View.GONE)
                reviewstitle.setVisibility(View.GONE)
                reviewstitle1.setVisibility(View.GONE)
                imagereviews1.setVisibility(View.GONE)
                imagereviews2.setVisibility(View.GONE)
                elipse_point.setVisibility(View.GONE)
                elipse_point2.setVisibility(View.GONE)
                star.setVisibility(View.GONE)
                star2.setVisibility(View.GONE)
                reviewstatus = 1
                val fragment = ReviewsGameFragment()
                val args = Bundle()
                args.putInt("gameid", gameID)
                args.putString("token", token)
                fragment.arguments = args
                val fragmentManager: FragmentManager = supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragmentContainer, fragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
            else if(reviewstatus == 1  && reviewsnumber ==2)
            {
                reviewsrating.setVisibility(View.VISIBLE)
                reviewsrating1.setVisibility(View.VISIBLE)
                reviewstitle.setVisibility(View.VISIBLE)
                reviewstitle1.setVisibility(View.VISIBLE)
                imagereviews1.setVisibility(View.VISIBLE)
                imagereviews2.setVisibility(View.VISIBLE)
                elipse_point.setVisibility(View.VISIBLE)
                elipse_point2.setVisibility(View.VISIBLE)
                star.setVisibility(View.VISIBLE)
                star2.setVisibility(View.VISIBLE)
                reviewstatus = 0
                val fragmentManager: FragmentManager = supportFragmentManager
                fragmentManager.popBackStack()
            }
            else if(reviewstatus == 1 && reviewsnumber ==1)
            {
                reviewsrating.setVisibility(View.VISIBLE)
                reviewstitle.setVisibility(View.VISIBLE)
                imagereviews1.setVisibility(View.VISIBLE)
                elipse_point.setVisibility(View.VISIBLE)
                star.setVisibility(View.VISIBLE)
                reviewstatus = 0
                val fragmentManager: FragmentManager = supportFragmentManager
                fragmentManager.popBackStack()
            }

        }

        // Botão para adiconar à wishlist
        addWishlistButton.setOnClickListener {
            addToWishlist()
        }

    }
}