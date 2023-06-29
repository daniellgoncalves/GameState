package com.example.gamestate.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamestate.R
import com.example.gamestate.ui.data.Home.SpinnerAdapter
import com.example.gamestate.ui.data.Library.RecViewLibraryAdapter
import com.example.gamestate.ui.data.RetroFitService
import com.example.gamestate.ui.data.ReviewsGameFragment
import com.example.gamestate.ui.data.ReviewsLibraryFragment
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class LibraryActivity : AppCompatActivity() {

    private var settings = arrayOf("Settings","Logout")
    private var images = intArrayOf(R.drawable.baseline_settings_24,R.drawable.baseline_logout_24)
    private var reviewstatus = 0
    private var noreviews = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        val spinnerHeader: Spinner = findViewById(R.id.home_header_spinner)
        val username: TextView = findViewById(R.id.homePage_user_text)
        val showallreviewstv: TextView = findViewById(R.id.showallreviewstv)
        val reviewtext1: TextView = findViewById(R.id.library_reviews_text1)
        val reviewtext2: TextView = findViewById(R.id.library_reviews_text2)
        val reviewimage1: ImageView = findViewById(R.id.library_reviews_image1)
        val reviewimage2: ImageView = findViewById(R.id.library_reviews_image2)
        val viewlibrary: View = findViewById(R.id.viewlibrary)
        val sharedPreferences = application.getSharedPreferences("login", Context.MODE_PRIVATE)
        val loginAutomatic = sharedPreferences.getString("username","")
        username.text = loginAutomatic

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

        val recyclerView = findViewById<RecyclerView>(R.id.library_recyclerview)
        val linearLayoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        val serverIP = resources.getString(R.string.server_ip)

        val retrofit = Retrofit.Builder()
            .baseUrl(serverIP)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(RetroFitService::class.java)

        val requestBodyUser = JsonObject()
        requestBodyUser.addProperty("username", loginAutomatic)

        val callUser = service.sendTopicByUser(requestBodyUser)

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
                                recyclerView.adapter = RecViewLibraryAdapter(topicNameList, topicImageList, topicIDList, forumID)
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

        val callReviewsuser = service.sendReviewByUser(requestBodyUser)

        val rReviewsuser = Runnable {
            callReviewsuser.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val res = response.body()?.string()
                        val responseJson = JSONObject(res!!)
                        if (responseJson.getInt("status") == 200)
                        {
                            val reviewsArray = responseJson.getJSONArray("reviewsbyusernames")
                            if(reviewsArray.length() == 1)
                            {
                                reviewtext1.setText(reviewsArray.getJSONObject(0).getString("title"))
                                Glide.with(applicationContext)
                                    .load(reviewsArray.getJSONObject(0).getString("image"))
                                    .centerCrop()
                                    .into(reviewimage1)
                            }
                            else if(reviewsArray.length() >= 2)
                            {
                                reviewtext1.setText(reviewsArray.getJSONObject(0).getString("title"))
                                Glide.with(applicationContext)
                                    .load(reviewsArray.getJSONObject(0).getString("image"))
                                    .centerCrop()
                                    .into(reviewimage1)
                                reviewtext2.setText(reviewsArray.getJSONObject(1).getString("title"))
                                Glide.with(applicationContext)
                                    .load(reviewsArray.getJSONObject(1).getString("image"))
                                    .centerCrop()
                                    .into(reviewimage2)
                            }

                        }
                        else {
                            noreviews = 1
                            reviewtext1.setVisibility(View.GONE)
                            reviewtext2.setVisibility(View.GONE)
                            reviewimage1.setVisibility(View.GONE)
                            reviewimage2.setVisibility(View.GONE)
                            viewlibrary.setVisibility(View.GONE)
                            showallreviewstv.setVisibility(View.GONE)
                        }
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(applicationContext, "Network Failure", Toast.LENGTH_SHORT).show()
                }
            })
        }
        val tReviewsuser = Thread(rReviewsuser)
        tReviewsuser.start()
        showallreviewstv.setOnClickListener {
            val fragment = ReviewsLibraryFragment()
            val args = Bundle()
            args.putString("username", loginAutomatic)
            fragment.arguments = args
            val fragmentManager: FragmentManager = supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainer, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

            if ( reviewstatus == 0) {
                reviewtext1.setVisibility(View.GONE)
                reviewtext2.setVisibility(View.GONE)
                reviewimage1.setVisibility(View.GONE)
                reviewimage2.setVisibility(View.GONE)
                viewlibrary.setVisibility(View.GONE)
                showallreviewstv.setText("Show only 2 reviews")
                reviewstatus = 1
            }
            else if(reviewstatus == 1 && noreviews==0)
            {
                reviewtext1.setVisibility(View.VISIBLE)
                reviewtext2.setVisibility(View.VISIBLE)
                reviewimage1.setVisibility(View.VISIBLE)
                reviewimage2.setVisibility(View.VISIBLE)
                viewlibrary.setVisibility(View.VISIBLE)
                showallreviewstv.setText("Show all reviews")
                reviewstatus = 0
                fragmentTransaction.hide(fragment)
            }
        }
    }
}