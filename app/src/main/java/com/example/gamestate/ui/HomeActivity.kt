package com.example.gamestate.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gamestate.R
import com.example.gamestate.ui.data.Home.RecViewHomeAdapter
import com.example.gamestate.ui.data.Home.SpinnerAdapter
import com.example.gamestate.ui.data.RetroFitService
import com.google.gson.JsonObject
import kotlinx.coroutines.delay
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Timer
import java.util.TimerTask

class HomeActivity : AppCompatActivity() {

    private var settings = arrayOf("Settings","Logout")
    private var images = intArrayOf(R.drawable.baseline_settings_24,R.drawable.baseline_logout_24)
    private  var idimg = ArrayList<Int>()

    private lateinit var userPicture: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val username: TextView = findViewById(R.id.homePage_user_text)
        val spin: Spinner = findViewById(R.id.home_header_spinner)

        val sharedPreferences = application.getSharedPreferences("login", Context.MODE_PRIVATE)
        val loginAutomatic = sharedPreferences.getString("username","")
        val token = sharedPreferences.getString("token","")

        userPicture = findViewById(R.id.homePage_user)

        val imageUriString = sharedPreferences.getString("imageUri", null)

        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)


            // Use the retrieved image URI
            Glide.with(this)
                .load(imageUri)
                .apply(RequestOptions()
                    .centerCrop()
                    .override(100, 100)) // Specify the desired dimensions of the ImageView
                .into(userPicture)
        }



        val searchGameText : EditText = findViewById(R.id.home_search_et)
            val firstImg : ImageView = findViewById(R.id.first_game)
            val secondImg : ImageView = findViewById(R.id.second_game)
            val thirdImg : ImageView = findViewById(R.id.thirdgame)
            val fourthImg : ImageView = findViewById(R.id.fourth_game)
            val fiftyImg : ImageView = findViewById(R.id.fifth_game)
            val sixtyImg : ImageView = findViewById(R.id.sixth_game)
        val recyclerView = findViewById<RecyclerView>(R.id.home_gameSearch_recyclerview)
        username.setText(loginAutomatic)

        val library: ImageButton = findViewById(R.id.homePage_library)
        val notificationbutton: ImageButton = findViewById(R.id.homePage_notifications)

        library.setOnClickListener {
            startActivity(Intent(this, LibraryActivity::class.java))
        }
        notificationbutton.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        username.setOnClickListener {
            val intent = Intent(this,ProfileActivity::class.java)
            startActivity(intent)
        }

        fun popularGames(){
            val serverIP = resources.getString(R.string.server_ip)
            val retrofit = Retrofit.Builder()
                .baseUrl(serverIP)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val userService = retrofit.create(RetroFitService::class.java)

            val call = userService.getPopularGames(token!!, "", "releasedate")

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
                            val popularGamesImg = jsonObject.getJSONArray("message")
                            val popularGames = ArrayList<String>()
                            for (i in 0 until popularGamesImg.length())
                            {
                                popularGames.add(popularGamesImg.getJSONObject(i).getString("image"))
                                idimg.add(popularGamesImg.getJSONObject(i).getInt("id"))
                            }
                            if (status == 200) {

                                Glide.with(applicationContext)
                                    .load(popularGames[0])
                                    .centerCrop()
                                    .into(firstImg)
                                Glide.with(applicationContext)
                                    .load(popularGames[1])
                                    .centerCrop()
                                    .into(secondImg)
                                Glide.with(applicationContext)
                                    .load(popularGames[2])
                                    .centerCrop()
                                    .into(thirdImg)
                                Glide.with(applicationContext)
                                    .load(popularGames[3])
                                    .centerCrop()
                                    .into(fourthImg)
                                Glide.with(applicationContext)
                                    .load(popularGames[4])
                                    .centerCrop()
                                    .into(fiftyImg)
                                Glide.with(applicationContext)
                                    .load(popularGames[5])
                                    .centerCrop()
                                    .into(sixtyImg)
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
        fun searchGame() {
            val nameText = searchGameText.text.toString()
            val serverIP = resources.getString(R.string.server_ip)
            val retrofit = Retrofit.Builder()
                .baseUrl(serverIP)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val userService = retrofit.create(RetroFitService::class.java)
            val requestBody = JsonObject()
            requestBody.addProperty("name", nameText)

            val call = userService.search(token!!, nameText)
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
                            val names = ArrayList<String>()
                            val gameID = ArrayList<Int>()
                          //  val intent = Intent(this,GameActivity::class.java)
                            //intent.putExtra("id",idimg[0]);
                            //startActivity(intent)
                            for (i in 0 until jsonObject.getJSONArray("game").length())
                            {
                                names.add(jsonObject.getJSONArray("game").getString(i))
                                gameID.add(jsonObject.getJSONArray("id").getInt(i))
                           }

                            if (status == 200) {

                                recyclerView.adapter = RecViewHomeAdapter(names, ContextCompat.getColor(applicationContext, R.color.gold20),gameID)
                                recyclerView.layoutManager = LinearLayoutManager(applicationContext)
                            }

                        } catch (e: JSONException) {
                            // Handle JSON parsing error
                            // ...
                        }
                    }

                    override fun onFailure(calll: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(applicationContext, "Network Failure", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
            }
            val t = Thread(r)
            t.start()
        }


        popularGames()

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
        fiftyImg.setOnClickListener {
            val intent = Intent(this,GameActivity::class.java)
            intent.putExtra("id",idimg[4]);
            startActivity(intent)
        }
        sixtyImg.setOnClickListener {
            val intent = Intent(this,GameActivity::class.java)
            intent.putExtra("id",idimg[5]);
            startActivity(intent)
        }
        searchGameText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                Timer().schedule(object: TimerTask() {
                    override fun run() {
                        searchGame()
                    }
                }, 500)

            }

        })
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

        //Verify if the app has notification permissions, if not ask for them

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { _: Boolean -> }

        if (ContextCompat.checkSelfPermission(applicationContext,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}





