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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gamestate.R
import com.example.gamestate.ui.data.Forum.RecViewForumAdapter
import com.example.gamestate.ui.data.Forum.SpinnerForumAdapter
import com.example.gamestate.ui.data.Home.SpinnerAdapter
import com.example.gamestate.ui.data.RetroFitService
import com.example.gamestate.ui.data.TopicDC
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

class ForumActivity : AppCompatActivity() {

    // Inicialização das variáveis do spinner de fórum
    private var settings = arrayOf("Trending topics","New topics","Most liked")
    private var images = intArrayOf(
        R.drawable.baseline_trending_up_24,
        R.drawable.baseline_access_time_24,
        R.drawable.baseline_heart_24)

    // Inicialização das variáveis do spinner de Logout
    private var settings1 = arrayOf("Settings","Logout")
    private var images1 = intArrayOf(R.drawable.baseline_settings_24,R.drawable.baseline_logout_24)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)


        // IP do servidor Nest
        val server_ip = resources.getString(R.string.server_ip)

        // Definição de variáveis (elementos XML)
        val spinnerHeader: Spinner = findViewById(R.id.home_header_spinner)
        val spinner: Spinner = findViewById(R.id.forum_filter)
        val recyclerView = findViewById<RecyclerView>(R.id.forum_recyclerview)
        val username: TextView = findViewById(R.id.homePage_user_text)
        val btnTopic : Button = findViewById(R.id.createTopic_button)
        val library: ImageButton = findViewById(R.id.homePage_library)
        val notificationbutton: ImageButton = findViewById(R.id.homePage_notifications)
        val homeButton: ImageButton = findViewById(R.id.home_home)
        val mapButton: ImageButton = findViewById(R.id.map_button)
        var userPicture: ImageView = findViewById(R.id.homePage_user)

        // Obtenção de dados sharedPreferences
        val sharedPreferences = application.getSharedPreferences("login", Context.MODE_PRIVATE)
        val loginAutomatic = sharedPreferences.getString("username","")
        val token = sharedPreferences.getString("token","")
        val imageUriString = sharedPreferences.getString("imageUri", null)

        val gameID = intent.getIntExtra("id",-1)

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

        // Funcionalidades dos restantes botões
        btnTopic.setOnClickListener {
            val intent = Intent(this,CreateTopicActivity::class.java)
            intent.putExtra("id",gameID)
            startActivity(intent)
        }

        mapButton.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("id", gameID)
            startActivity(intent)
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
        val customAdapter = SpinnerForumAdapter(applicationContext, images, settings)
        spinner.adapter = customAdapter

        // População do spinner do fórum
        val customAdapterSettings = SpinnerAdapter(applicationContext, images1, settings1)
        spinnerHeader.adapter = customAdapterSettings



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

        if (gameID == -1) {
            Toast.makeText(applicationContext, "Game ID missing", Toast.LENGTH_SHORT).show()
        } else {
            //Obter dados do jogo
            val retrofit = Retrofit.Builder()
                .baseUrl(server_ip)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(RetroFitService::class.java)

            val requestBody = JsonObject()
            requestBody.addProperty("id", gameID)

            val callGame = service.searchByID(token!!, gameID)

            val rGame = Runnable {
                callGame.enqueue(object : Callback<ResponseBody> {
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
            val tGame = Thread(rGame)
            tGame.start()

            // Obter tópicos
            val callTopic = service.searchTopicByGameID(token!!, gameID)

            val mainHandler = Handler(Looper.getMainLooper())

            val rTopic = Runnable {
                callTopic.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val res = response.body()?.string()
                            val responseJson = JSONObject(res!!)
                            if (responseJson.getInt("status") == 200)
                            {
                                val topicList = ArrayList<TopicDC>()
                                val topicArray = responseJson.getJSONArray("topics")

                                for(i in 0 until topicArray.length()) {
                                    topicList.add(TopicDC(topicArray.getJSONObject(i).getString("_id"), topicArray.getJSONObject(i).getString("name")))
                                }

                                mainHandler.post {
                                    recyclerView.adapter = RecViewForumAdapter(topicList, gameID, ContextCompat.getColor(applicationContext, R.color.gold20))
                                    recyclerView.layoutManager = LinearLayoutManager(applicationContext)
                                }
                            }
                            else {
//                                Toast.makeText(applicationContext, responseJson.getString("message"), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(applicationContext, "Network Failure", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            val tTopic = Thread(rTopic)
            tTopic.start()
        }
    }
}