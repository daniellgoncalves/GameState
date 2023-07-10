package com.example.gamestate.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PointF.length
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gamestate.R
import com.example.gamestate.ui.data.Home.SpinnerAdapter
import com.example.gamestate.ui.data.RetroFitService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    // Inicialização das variáveis do spinner de Logout e outras variáveis
    private lateinit var googleMap: GoogleMap

    private var settings1 = arrayOf("Settings","Logout")
    private var images1 = intArrayOf(R.drawable.baseline_settings_24,R.drawable.baseline_logout_24)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // IP do servidor Nest
        val server_ip = resources.getString(R.string.server_ip)

        // Definição de variáveis (elementos XML)
        val spinnerHeader: Spinner = findViewById(R.id.home_header_spinner)
        val username: TextView = findViewById(R.id.homePage_user_text)
        val notificationbutton: ImageButton = findViewById(R.id.homePage_notifications)
        val homeButton: ImageButton = findViewById(R.id.home_home)
        val library: ImageButton = findViewById(R.id.homePage_library)
        var userPicture: ImageView = findViewById(R.id.homePage_user)

        // Obtenção de dados sharedPreferences
        val sharedPreferences = application.getSharedPreferences("login", Context.MODE_PRIVATE)
        val loginAutomatic = sharedPreferences.getString("username", "")
        val token = sharedPreferences.getString("token", "")

        val imageUriString = sharedPreferences.getString("imageUri", null)

        username.text = loginAutomatic

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

        val customAdapterSettings = SpinnerAdapter(applicationContext, images1, settings1)
        spinnerHeader.adapter = customAdapterSettings

        val gameID = intent.getIntExtra("id", -1)

        if (gameID == -1) {
            Toast.makeText(applicationContext, "Game ID missing", Toast.LENGTH_SHORT).show()
        } else {
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
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            val res = response.body()?.string()
                            val responseJson = JSONObject(res!!)
                            if (responseJson.getInt("status") == 200) {
                                val name: TextView = findViewById(R.id.gameName_tv)
                                val developer: TextView = findViewById(R.id.gameCompany_tv)
                                val releaseDate: TextView = findViewById(R.id.gameReleaseDate_tv)
                                val gameImage: ImageView = findViewById(R.id.selectedGame_iv)
                                val developerImage: ImageView = findViewById(R.id.gameCompany_iv)

                                val date =
                                    responseJson.getJSONObject("message").getString("release_date")

                                val inputFormat =
                                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val outputFormat =
                                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                                val parsedDate = inputFormat.parse(date)
                                val formattedDate = outputFormat.format(parsedDate)

                                name.text = responseJson.getJSONObject("message").getString("name")
                                developer.text =
                                    responseJson.getJSONObject("message").getJSONArray("developers")
                                        .getJSONObject(0).getString("name")
                                releaseDate.text = formattedDate
                                val gameImageUrl =
                                    responseJson.getJSONObject("message").getString("image")
                                val developerImageUrl =
                                    responseJson.getJSONObject("message").getJSONArray("developers")
                                        .getJSONObject(0).getString("image")

                                Glide.with(applicationContext)
                                    .load(gameImageUrl)
                                    .centerCrop()
                                    .into(gameImage)

                                Glide.with(applicationContext)
                                    .load(developerImageUrl)
                                    .centerCrop()
                                    .into(developerImage)
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
                        Toast.makeText(applicationContext, "Network Failure", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
            }
            val tGame = Thread(rGame)
            tGame.start()
        }



        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val countryNames = mutableListOf<String>()
        val countryCounts = mutableListOf<Int>()
        val latLngList = mutableListOf<LatLng>()

        fun getCountries() {
            val gameID = intent.getIntExtra("id", -1)
            val sharedPreferences = application.getSharedPreferences("login", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("token", "")

            val serverIP = resources.getString(R.string.server_ip)

            val retrofit = Retrofit.Builder()
                .baseUrl(serverIP)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(RetroFitService::class.java)

            val requestBody = JsonObject()
            requestBody.addProperty("id", gameID)

            val callGame = service.getCountries(token!!, gameID)

            val rGame = Runnable {
                callGame.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            val res = response.body()?.string()
                            val responseJson = JSONObject(res!!)
                            if (responseJson.getInt("status") == 200) {

                                val countryName = responseJson.getJSONArray("countryNames")
                                val countryCount = responseJson.getJSONArray("counts")

                                for (i in 0 until countryName.length()) {
                                    countryNames.add(countryName.getString(i))
                                    countryCounts.add(countryCount.getInt(i))
                                }

                                val geocoder = Geocoder(this@MapActivity)
                                for (countryName in countryNames) {
                                    try {
                                        val addresses = geocoder.getFromLocationName(countryName, 1)
                                        if (addresses != null) {
                                            if (addresses.isNotEmpty()) {
                                                val address = addresses[0]
                                                val latLng = LatLng(address.latitude, address.longitude)
                                                latLngList.add(latLng)

                                            } else {
                                                Toast.makeText(this@MapActivity, "Failed to get coordinates for the capital city of $countryName", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }
                                }

                                addPinToMap(latLngList, countryNames, countryCounts)
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
                        Toast.makeText(applicationContext, "Network Failure", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
            }
            val tGame = Thread(rGame)
            tGame.start()
        }

        getCountries()

    }

    private fun addPinToMap(latLngList: List<LatLng>, countriesNames: List<String>, countriesCounts: List<Int>) {
        for(i in countriesNames.indices) {
            val latLng = latLngList[i]
            val markerTitle = "${countriesNames[i]}: ${countriesCounts[i]}"

            val markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.pin)

            val marker = googleMap.addMarker(MarkerOptions().position(latLng).title(markerTitle).icon(markerIcon))
            marker?.showInfoWindow()
            val DEFAULT_ZOOM = 0.0f
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM))
        }
    }


    override fun onMarkerClick(p0: Marker?): Boolean {
        TODO("Not yet implemented")
    }
}