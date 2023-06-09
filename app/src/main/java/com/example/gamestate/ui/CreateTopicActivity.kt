package com.example.gamestate.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
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
import java.util.*

class CreateTopicActivity : AppCompatActivity() {

    // Inicialização das variáveis do spinner de Logout
    private var settings = arrayOf("Settings","Logout")
    private var images = intArrayOf(R.drawable.baseline_settings_24,R.drawable.baseline_logout_24)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_topic)

        // IP do servidor Nest
        val server_ip = resources.getString(R.string.server_ip)

        // Definição de variáveis (elementos XML)
        val username: TextView = findViewById(R.id.homePage_user_text)
        val spin: Spinner = findViewById(R.id.home_header_spinner)
        val title: EditText = findViewById(R.id.createTopic_title_et)
        val topic: EditText = findViewById(R.id.createTopic_text_et)
        val btnTopic : Button = findViewById(R.id.createTopic_button)
        val library: ImageButton = findViewById(R.id.homePage_library)
        val notificationbutton: ImageButton = findViewById(R.id.homePage_notifications)
        val homeButton: ImageButton = findViewById(R.id.home_home)
        var userPicture: ImageView = findViewById(R.id.homePage_user)

        // Obtenção de dados sharedPreferences
        val sharedPreferences = application.getSharedPreferences("login", Context.MODE_PRIVATE)
        val loginAutomatic = sharedPreferences.getString("username","")
        val userid = sharedPreferences.getString("userid","")
        val token = sharedPreferences.getString("token","")
        val imageUriString = sharedPreferences.getString("imageUri", null)

        val gameID = intent.getIntExtra("id",0);

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
        val Adapter = SpinnerAdapter(applicationContext, images, settings)
        spin.adapter = Adapter

        // Obter dados do jogo
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
                            val gameImage : ImageView = findViewById(R.id.selectedGame_iv)
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


        // Criação de tópico
        fun topicinit(){
            val titleText = title.text.toString()
            val topicText = topic.text.toString()
            val requestBody1 = JsonObject()
            val likeDislike = 0

            requestBody1.addProperty("name", titleText)
            requestBody1.addProperty("text", topicText)
            requestBody1.addProperty("user_id", userid)
            requestBody1.addProperty("forum_id", gameID)

            val likeDislikeObject = JsonObject()

            likeDislikeObject.addProperty("username", username.text.toString())
            likeDislikeObject.addProperty("likeDislike", likeDislike)

            requestBody1.add("likeDislike", likeDislikeObject)

            val call1 = service.createTopic(token!!, requestBody1)

            val r1 = Runnable {
                call1.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val res = response.body()?.string()
                            val responseJson = JSONObject(res!!)
                            val msm = responseJson.getString("message")
                            if (responseJson.getInt("status") == 200)
                            {
                                Toast.makeText(applicationContext, msm, Toast.LENGTH_SHORT)
                                    .show()
                            }
                            else {
                                Toast.makeText(applicationContext, msm, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(applicationContext, "Network Failure", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            val t1 = Thread(r1)
            t1.start()

            val intent = Intent(this,ForumActivity::class.java)
            intent.putExtra("id",gameID);
            startActivity(intent)
        }

        // Funcionalidades dos restantes botões
        btnTopic.setOnClickListener {
            topicinit()
        }
    }
}