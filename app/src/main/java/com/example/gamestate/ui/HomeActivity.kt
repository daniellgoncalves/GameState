package com.example.gamestate.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamestate.R
import com.example.gamestate.ui.data.Home.RecViewHomeAdapter
import com.example.gamestate.ui.data.Home.SpinnerAdapter
import com.example.gamestate.ui.data.RetroFitService
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeActivity : AppCompatActivity() {

    private var settings = arrayOf("Settings","Logout")
    private var images = intArrayOf(R.drawable.baseline_settings_24,R.drawable.baseline_logout_24)
    private  var idimg = ArrayList<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val username: TextView = findViewById(R.id.home_user_text)
        val spin: Spinner = findViewById(R.id.home_header_spinner)
        val sharedPreferences = application.getSharedPreferences("login", Context.MODE_PRIVATE)
        val loginAutomatic = sharedPreferences.getString("username","")
        val searchGameText : EditText = findViewById(R.id.home_search_edit_text)
        val firstImg : ImageView = findViewById(R.id.first_game)
        val secondImg : ImageView = findViewById(R.id.second_game)
        val thirdImg : ImageView = findViewById(R.id.thirdgame)
        val fourthImg : ImageView = findViewById(R.id.fourth_game)
        val fiftyImg : ImageView = findViewById(R.id.fifth_game)
        val sixtyImg : ImageView = findViewById(R.id.sixth_game)
        val recyclerView = findViewById<RecyclerView>(R.id.home_games_recyclerview)
        username.setText(loginAutomatic)


        fun popularGames(){
            val serverIP = resources.getString(R.string.server_ip)
            val retrofit = Retrofit.Builder()
                .baseUrl(serverIP)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val userService = retrofit.create(RetroFitService::class.java)
            val requestBody = JsonObject()
            val call = userService.sendGame(requestBody)
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
                            val popularGamesImg =jsonObject.getJSONArray("populargames")
                            val popularGames = ArrayList<String>()
                            for (i in 0 until popularGamesImg.length())
                            {
                                if(i%2==0)
                                {
                                    popularGames.add(popularGamesImg.getString(i))
                                }
                                else
                                {
                                    idimg.add(popularGamesImg.getInt(i))

                                }

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

            val call = userService.sendGame(requestBody)
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
                            val msm =jsonObject.getJSONArray("game")
                            val Names = ArrayList<String>()
                            for (i in 0 until msm.length())
                            {
                                Names.add(msm.getString(i))
                           }
                            if (status == 200) {

                                recyclerView.adapter = RecViewHomeAdapter(Names  , ContextCompat.getColor(applicationContext, R.color.gold20))
                                recyclerView.layoutManager = LinearLayoutManager(applicationContext)
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
                searchGame()

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
        val forumAdapter = SpinnerAdapter(applicationContext, images, settings)
        spin.adapter = forumAdapter
    }
}





