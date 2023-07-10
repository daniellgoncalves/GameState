package com.example.gamestate.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gamestate.R
import com.example.gamestate.ui.data.Home.SpinnerAdapter
import com.example.gamestate.ui.data.Profile.RecViewProfileReviewsAdapter
import com.example.gamestate.ui.data.Profile.RecViewProfileTopicsAdapter
import com.example.gamestate.ui.data.Profile.RecViewProfileWishlistAdapter
import com.example.gamestate.ui.data.RetroFitService
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.ArrayList

class ProfileActivity : AppCompatActivity() {

    // Inicialização das variáveis do spinner de Logout e outras variáveis
    private var settings = arrayOf("Settings","Logout")
    private var images = intArrayOf(R.drawable.baseline_settings_24,R.drawable.baseline_logout_24)
    private  var idimg = ArrayList<Int>()

    private lateinit var profilePicture: ImageView
    private lateinit var uploadProfilePicture: Button

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var userPicture: ImageView

    private val SELECT_IMAGE_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

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

        profilePicture = findViewById(R.id.profile_picture)
        uploadProfilePicture = findViewById(R.id.upload_picture_button)
        userPicture = findViewById(R.id.homePage_user)

        val library: ImageButton = findViewById(R.id.homePage_library)
        val notificationbutton: ImageButton = findViewById(R.id.homePage_notifications)
        val homeButton: ImageButton = findViewById(R.id.home_home)

        var profileUsername = findViewById<TextView>(R.id.profile_name_tv)

        // Obtenção de dados sharedPreferences
        sharedPreferences = application.getSharedPreferences("login", Context.MODE_PRIVATE)
        val loginAutomatic = sharedPreferences.getString("username","")
        val token = sharedPreferences.getString("token","")
        val userID = sharedPreferences.getString("userid","")

        val imageUriString = sharedPreferences.getString("imageUri", null)

        username.text = loginAutomatic

        // Funcionalidade dos botões de header e footer
        uploadProfilePicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*"
            startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE)
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

        // Obtenção da imagem de perfil
        if (imageUriString!! != null) {

            // Use the retrieved image URI
            Glide.with(this)
                .load(imageUriString)
                .apply(RequestOptions()
                    .centerCrop()
                    .override(800, 800)) // Specify the desired dimensions of the ImageView
                .into(profilePicture)

            Glide.with(this)
                .load(imageUriString)
                .apply(RequestOptions()
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

        profileUsername.text = loginAutomatic

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
        val topicsRecyclerView = findViewById<RecyclerView>(R.id.profile_topics_recyclerview)
        val linearLayoutManagerTopics = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        val reviewsRecyclerView = findViewById<RecyclerView>(R.id.profile_reviews_recyclerview)
        val linearLayoutManagerReviews = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        val wishlistRecyclerView = findViewById<RecyclerView>(R.id.profile_wishlist_recyclerview)
        val linearLayoutManagerWishlist = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        // Obter wishlist
        fun getWishlist() {
            val retrofit = Retrofit.Builder()
                .baseUrl(server_ip)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(RetroFitService::class.java)

            val callUser = service.getWishlist(token!!, userID!!)

            val mainHandler = Handler(Looper.getMainLooper())

            val r = Runnable {
                callUser.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(callUser: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val res = response.body()?.string()
                            val responseJson = JSONObject(res!!)
                            if (responseJson.getInt("status") == 200) {
                                if (responseJson.getJSONArray("message").length() != 0) {
                                    val wishlistNameList = ArrayList<String>()
                                    val wishlistImageList = ArrayList<String>()
                                    val gameIDList = ArrayList<Int>()
                                    val wishlistArray = responseJson.getJSONArray("message")

                                    for (i in 0 until wishlistArray.length()) {
                                        wishlistNameList.add(
                                            wishlistArray.getJSONObject(i).getString("gameName")
                                        )
                                        wishlistImageList.add(
                                            wishlistArray.getJSONObject(i).getString("gameImages")
                                        )
                                        gameIDList.add(
                                            wishlistArray.getJSONObject(i).getInt("wishlistGame")
                                        )
                                    }

                                    mainHandler.post {
                                        wishlistRecyclerView.adapter =
                                            RecViewProfileWishlistAdapter(
                                                wishlistNameList,
                                                wishlistImageList,
                                                gameIDList
                                            )
                                        wishlistRecyclerView.layoutManager =
                                            linearLayoutManagerWishlist
                                    }
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        responseJson.getString("message"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
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

        getWishlist()

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
                                    topicsRecyclerView.adapter = RecViewProfileTopicsAdapter(topicNameList, topicImageList, topicIDList, forumID)
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
                                val reviewsArray = responseJson.getJSONArray("reviewsbyusernames")


                                for (i in 0 until reviewsArray.length()) {
                                    reviewRatingList.add(reviewsArray.getJSONObject(i).getString("rating"))
                                    reviewTitleList.add(reviewsArray.getJSONObject(i).getString("title"))
                                    reviewImageList.add(reviewsArray.getJSONObject(i).getString("image"))
                                    reviewTextList.add(reviewsArray.getJSONObject(i).getString("text"))
                                }

                                mainHandler.post {
                                    reviewsRecyclerView.adapter = RecViewProfileReviewsAdapter(reviewRatingList, reviewImageList, reviewTitleList, reviewTextList)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            if (imageUri != null) {
                // Display the selected image using Glide
                Glide.with(this)
                    .load(imageUri)
                    .centerCrop()
                    .override(1000, 1000)
                    .into(profilePicture)

                Glide.with(this)
                    .load(imageUri)
                    .centerCrop()
                    .override(100, 100)
                    .into(userPicture)

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString("imageUri",imageUri.toString())
                editor.apply()

                // Upload the image to the server (you can use the uploadImage() method from your existing code)
                val imageFile = File(imageUri.path)
                uploadImage(imageFile)
            }
        }
    }


    private fun uploadImage(imageFile: File) {
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
        val body = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

        val token = sharedPreferences.getString("token","")
        val userID = sharedPreferences.getString("userid","")

        val serverIP = resources.getString(R.string.server_ip)

        val retrofit = Retrofit.Builder()
            .baseUrl(serverIP)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(RetroFitService::class.java)
        val call = service.uploadImage(token!!, userID!!, body) // Replace authorizationHeader and userId with actual values
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    // Image uploaded successfully
                    Toast.makeText(
                        applicationContext,
                        "Image uploaded successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Handle server error
                    Toast.makeText(
                        applicationContext,
                        "Failed to upload image. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle network error
                Toast.makeText(
                    applicationContext,
                    "Failed to upload image. Please check your network connection.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}