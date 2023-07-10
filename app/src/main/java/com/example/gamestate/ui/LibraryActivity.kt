package com.example.gamestate.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.Image
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
import com.example.gamestate.ui.data.Home.SpinnerAdapter
import com.example.gamestate.ui.data.Library.RecViewLibraryAdapter
import com.example.gamestate.ui.data.Library.RecViewLibraryReviewsAdapter
import com.example.gamestate.ui.data.Profile.RecViewProfileReviewsAdapter
import com.example.gamestate.ui.data.Profile.RecViewProfileTopicsAdapter
import com.example.gamestate.ui.data.RetroFitService
import com.example.gamestate.ui.data.ReviewsGameFragment
import com.example.gamestate.ui.data.ReviewsLibraryFragment
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class LibraryActivity : AppCompatActivity() {

    // Inicialização das variáveis do spinner de Logout e outras variáveis
    private var settings = arrayOf("Settings","Logout")
    private var images = intArrayOf(R.drawable.baseline_settings_24,R.drawable.baseline_logout_24)
    private  var idimg = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        // IP do servidor Nest
        val server_ip = resources.getString(R.string.server_ip)

        // Definição de variáveis (elementos XML)
        val spinnerHeader: Spinner = findViewById(R.id.home_header_spinner)
        val username: TextView = findViewById(R.id.homePage_user_text)

        val firstImg : ImageView = findViewById(R.id.first_game)
        val secondImg : ImageView = findViewById(R.id.second_game)
        val thirdImg : ImageView = findViewById(R.id.third_game)
        val fourthImg : ImageView = findViewById(R.id.fourth_game)
        val fifthImg : ImageView = findViewById(R.id.fifth_game)
        val sixthImg : ImageView = findViewById(R.id.sixth_game)

        val firstGameStatus : ImageView = findViewById(R.id.gameStatus_firstGame)
        val secondGameStatus : ImageView = findViewById(R.id.gameStatus_secondGame)
        val thirdGameStatus : ImageView = findViewById(R.id.gameStatus_thirdGame)
        val fourthGameStatus : ImageView = findViewById(R.id.gameStatus_fourthGame)
        val fifthGameStatus : ImageView = findViewById(R.id.gameStatus_fifthGame)
        val sixthGameStatus : ImageView = findViewById(R.id.gameStatus_sixthGame)

        val firstRating : TextView = findViewById(R.id.gameRating_firstGame)
        val secondRating : TextView = findViewById(R.id.gameRating_secondGame)
        val thirdRating : TextView = findViewById(R.id.gameRating_thirdGame)
        val fourthRating : TextView = findViewById(R.id.gameRating_fourthGame)
        val fifthRating : TextView = findViewById(R.id.gameRating_fifthGame)
        val sixthRating : TextView = findViewById(R.id.gameRating_sixthGame)

        val firstRatingBase : LinearLayout = findViewById(R.id.rating_base_firstgame)
        val secondRatingBase : LinearLayout = findViewById(R.id.rating_base_secondgame)
        val thirdRatingBase : LinearLayout = findViewById(R.id.rating_base_thirdgame)
        val fourthRatingBase : LinearLayout = findViewById(R.id.rating_base_fourthgame)
        val fifthRatingBase : LinearLayout = findViewById(R.id.rating_base_fifthgame)
        val sixthRatingBase : LinearLayout = findViewById(R.id.rating_base_sixthgame)

        val firstRatingStar : ImageView = findViewById(R.id.star_firstgame)
        val secondRatingStar : ImageView = findViewById(R.id.star_secondgame)
        val thirdRatingStar : ImageView = findViewById(R.id.star_thirdgame)
        val fourthRatingStar : ImageView = findViewById(R.id.star_fourthgame)
        val fifthRatingStar : ImageView = findViewById(R.id.star_fifthgame)
        val sixthRatingStar : ImageView = findViewById(R.id.star_sixthgame)

        val notificationbutton: ImageButton = findViewById(R.id.homePage_notifications)
        val homeButton: ImageButton = findViewById(R.id.home_home)
        var userPicture: ImageView = findViewById(R.id.homePage_user)

        // Obtenção de dados sharedPreferences
        val sharedPreferences = application.getSharedPreferences("login", Context.MODE_PRIVATE)
        val loginAutomatic = sharedPreferences.getString("username","")
        val userID = sharedPreferences.getString("userid","")
        val token = sharedPreferences.getString("token","")

        val imageUriString = sharedPreferences.getString("imageUri", null)

        username.text = loginAutomatic

        // Funcionalidade dos botões de header e footer
        username.setOnClickListener {
            val intent = Intent(this,ProfileActivity::class.java)
            startActivity(intent)
        }
        homeButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        notificationbutton.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
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

        // População do spinner de logout
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

        // Inicialização de listas
        var images = arrayListOf<ImageView>(firstImg, secondImg, thirdImg, fourthImg, fifthImg, sixthImg)
        var gameStatusImg = arrayListOf<ImageView>(firstGameStatus, secondGameStatus, thirdGameStatus, fourthGameStatus, fifthGameStatus, sixthGameStatus)
        var ratingsImg = arrayListOf<TextView>(firstRating, secondRating, thirdRating, fourthRating, fifthRating, sixthRating)
        var ratingBases = arrayListOf<LinearLayout>(firstRatingBase, secondRatingBase, thirdRatingBase, fourthRatingBase, fifthRatingBase, sixthRatingBase)
        var ratingStars = arrayListOf<ImageView>(firstRatingStar, secondRatingStar, thirdRatingStar, fourthRatingStar, fifthRatingStar, sixthRatingStar)


        // Obter jogos subscritos
        fun subscribedGames() {
            val retrofit = Retrofit.Builder()
                .baseUrl(server_ip)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val subscribedGames = retrofit.create(RetroFitService::class.java)

            val requestBody = JsonObject()
            requestBody.addProperty("user_id", userID)

            val call = subscribedGames.getReviewsByUser(token!!, userID!!)
            val r = Runnable {
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        val res = response.body()?.string()
                        try {
                            val jsonObject = JSONObject(res!!)
                            val status = jsonObject.getInt("status")
                            val subscribedGamesImg = jsonObject.getJSONObject("subscribedgames").getJSONArray("subscribedgames")
                            val subscribedGames = ArrayList<String>()

                            val ratingsArray = jsonObject.getJSONObject("ratings").getJSONArray("ratings")
                            val ratings = ArrayList<String>()

                            val gameStatusArray = jsonObject.getJSONObject("gameStatus").getJSONArray("gameStatus")
                            val gameStatus = ArrayList<Int>()

                            for (i in 0 until subscribedGamesImg.length())
                            {
                                if(i%2==0)
                                {
                                    subscribedGames.add(subscribedGamesImg.getString(i))
                                }
                                else
                                {
                                    idimg.add(subscribedGamesImg.getInt(i))

                                }

                            }
                            for (i in 0 until gameStatusArray.length())
                            {
                                gameStatus.add(gameStatusArray.getInt(i))
                            }
                            for (i in 0 until ratingsArray.length())
                            {
                                ratings.add(ratingsArray.getString(i))
                            }

                            if (status == 200) {

                                for (i in 0 until subscribedGames.count()) {
                                    Glide.with(applicationContext)
                                        .load(subscribedGames[i])
                                        .centerCrop()
                                        .into(images[i])

                                    when (gameStatus[i]) {
                                        0 -> {
                                            Glide.with(applicationContext)
                                                .load(R.drawable.questionmark)
                                                .centerCrop()
                                                .into(gameStatusImg[i])
                                            gameStatusImg[i].setBackgroundResource(R.drawable.strokegamestatus)
                                            ratingBases[i].setBackgroundResource(R.drawable.libraryratingbase)
                                            ratingStars[i].setBackgroundResource(R.drawable.star1)
                                        }
                                        1 -> {
                                            Glide.with(applicationContext)
                                                .load(R.drawable.accept)
                                                .centerCrop()
                                                .into(gameStatusImg[i])
                                            gameStatusImg[i].setBackgroundResource(R.drawable.strokegamestatus)
                                            ratingBases[i].setBackgroundResource(R.drawable.libraryratingbase)
                                            ratingStars[i].setBackgroundResource(R.drawable.star1)
                                        }
                                        2 -> {
                                            Glide.with(applicationContext)
                                                .load(R.drawable.playbutton)
                                                .centerCrop()
                                                .into(gameStatusImg[i])
                                            gameStatusImg[i].setBackgroundResource(R.drawable.strokegamestatus)
                                            ratingBases[i].setBackgroundResource(R.drawable.libraryratingbase)
                                            ratingStars[i].setBackgroundResource(R.drawable.star1)
                                        }
                                        3 -> {
                                            Glide.with(applicationContext)
                                                .load(R.drawable.pause)
                                                .centerCrop()
                                                .into(gameStatusImg[i])
                                            gameStatusImg[i].setBackgroundResource(R.drawable.strokegamestatus)
                                            ratingBases[i].setBackgroundResource(R.drawable.libraryratingbase)
                                            ratingStars[i].setBackgroundResource(R.drawable.star1)
                                        }
                                        4 -> {
                                            Glide.with(applicationContext)
                                                .load(R.drawable.stop)
                                                .centerCrop()
                                                .into(gameStatusImg[i])
                                            gameStatusImg[i].setBackgroundResource(R.drawable.strokegamestatus)
                                            ratingBases[i].setBackgroundResource(R.drawable.libraryratingbase)
                                            ratingStars[i].setBackgroundResource(R.drawable.star1)
                                        }
                                    }

                                    ratingsImg[i].text = ratings[i]
                                }
                            }

                        } catch (e: JSONException) {
                            // Handle JSON parsing error
                            // ...
                        }
                    }

                    override fun onFailure(calll: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
            }
            val t = Thread(r)
            t.start()
        }

        subscribedGames()

        // Funcionalidades do clique em cada jogo
        firstImg.setOnClickListener {
            val intent = Intent(this,GameActivity::class.java)
            intent.putExtra("id",idimg[0]);
            startActivity(intent)
        }
        secondImg.setOnClickListener {
            val intent = Intent(this,GameActivity::class.java)
            intent.putExtra("id",idimg[1]);
            startActivity(intent)
        }
        thirdImg.setOnClickListener {
            val intent = Intent(this,GameActivity::class.java)
            intent.putExtra("id",idimg[2]);
            startActivity(intent)
        }
        fourthImg.setOnClickListener {
            val intent = Intent(this,GameActivity::class.java)
            intent.putExtra("id",idimg[3]);
            startActivity(intent)
        }
        fifthImg.setOnClickListener {
            val intent = Intent(this,GameActivity::class.java)
            intent.putExtra("id",idimg[4]);
            startActivity(intent)
        }
        sixthImg.setOnClickListener {
            val intent = Intent(this,GameActivity::class.java)
            intent.putExtra("id",idimg[5]);
            startActivity(intent)
        }

        // Inicialização dos RecyclerView de tópicos e reviews
        val topicsRecyclerView = findViewById<RecyclerView>(R.id.library_topics_recyclerview)
        val linearLayoutManagerTopics = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        val reviewsRecyclerView = findViewById<RecyclerView>(R.id.library_reviews_recyclerview)
        val linearLayoutManagerReviews = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        // Obter tópicos
        fun getTopics() {
            val retrofit = Retrofit.Builder()
                .baseUrl(server_ip)
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
                            if (responseJson.getInt("status") == 200)
                            {
                                val topicNameList = ArrayList<String>()
                                val topicImageList = ArrayList<String>()
                                val topicIDList = ArrayList<String>()
                                val topicsArray = responseJson.getJSONObject("message").getJSONArray("topics")
                                val imagesArray = responseJson.getJSONObject("message").getJSONArray("images")
                                val forumID = ArrayList<Int>()
                                val gameID = responseJson.getJSONObject("message").getJSONArray("topics")

                                for (i in 0 until topicsArray.length()) {
                                    topicNameList.add(topicsArray.getJSONObject(i).getString("name"))
                                    topicImageList.add(imagesArray.getString(i))
                                    topicIDList.add(topicsArray.getJSONObject(i).getString("_id"))
                                    forumID.add(gameID.getJSONObject(i).getInt("forum_id"))
                                }

                                mainHandler.post {
                                    topicsRecyclerView.adapter = RecViewLibraryAdapter(topicNameList, topicImageList, topicIDList, forumID)
                                    topicsRecyclerView.layoutManager = linearLayoutManagerTopics
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

        getTopics()

        // Obter reviews
        fun getReviews() {
            val retrofit = Retrofit.Builder()
                .baseUrl(server_ip)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(RetroFitService::class.java)

            val callUser = service.findByUser(token!!, userID!!)

            val mainHandler = Handler(Looper.getMainLooper())

            val r = Runnable {
                callUser.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(callUser: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val res = response.body()?.string()
                            val responseJson = JSONObject(res!!)
                            if (responseJson.getInt("status") == 200)
                            {

                                val reviewRatingList = ArrayList<String>()
                                val reviewTitleList = ArrayList<String>()
                                val reviewTextList = ArrayList<String>()
                                val reviewImageList = ArrayList<String>()
                                val reviewIDList = ArrayList<String>()
                                val forumID = ArrayList<Int>()
                                val reviewsArray = responseJson.getJSONArray("reviewsbyusernames")


                                for (i in 0 until reviewsArray.length()) {
                                    reviewRatingList.add(reviewsArray.getJSONObject(i).getString("rating"))
                                    reviewTitleList.add(reviewsArray.getJSONObject(i).getString("title"))
                                    reviewImageList.add(reviewsArray.getJSONObject(i).getString("image"))
                                    reviewTextList.add(reviewsArray.getJSONObject(i).getString("text"))
                                    reviewIDList.add(reviewsArray.getJSONObject(i).getString("_id"))
                                    forumID.add(reviewsArray.getJSONObject(i).getInt("forum_id"))
                                }

                                mainHandler.post {
                                    reviewsRecyclerView.adapter = RecViewLibraryReviewsAdapter(reviewRatingList, reviewImageList, reviewTitleList, reviewTextList, reviewIDList, forumID)
                                    reviewsRecyclerView.layoutManager = linearLayoutManagerReviews
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
        getReviews()
    }
}