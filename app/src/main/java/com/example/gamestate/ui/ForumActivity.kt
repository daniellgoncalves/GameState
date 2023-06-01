package com.example.gamestate.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestate.R
import com.example.gamestate.ui.data.Forum.RecViewForumAdapter
import com.example.gamestate.ui.data.Forum.SpinnerForumAdapter
import com.example.gamestate.ui.data.Home.SpinnerAdapter

class ForumActivity : AppCompatActivity() {
    private var settings = arrayOf("Trending topics","New topics","Most liked")
    private var images = intArrayOf(
        R.drawable.baseline_trending_up_24,
        R.drawable.baseline_access_time_24,
        R.drawable.baseline_heart_24)

    private var settings1 = arrayOf("Settings","Logout")
    private var images1 = intArrayOf(R.drawable.baseline_settings_24,R.drawable.baseline_logout_24)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)

        val spinner: Spinner = findViewById(R.id.home_header_spinner)
        val spinnerforum: Spinner = findViewById(R.id.forum_filter)
        val username: TextView = findViewById(R.id.home_user_text)
        val sharedPreferences = application.getSharedPreferences("login", Context.MODE_PRIVATE)
        val loginAutomatic = sharedPreferences.getString("username","")
        username.setText(loginAutomatic)

        val CustomAdaptersettings = SpinnerAdapter(applicationContext, images1, settings1)
        spinner.adapter = CustomAdaptersettings

        val CustomAdapter = SpinnerForumAdapter(applicationContext, images, settings)
        spinnerforum.adapter = CustomAdapter

        val recyclerView = findViewById<RecyclerView>(R.id.forum_recyclerview)
        recyclerView.adapter = RecViewForumAdapter(listOf("Gosto mais dos antigos",
            "Estou preso nesta quest",
            "Bug no início do jogo",
            "Melhor jogo do ano?",
            "A personagem é muito lenta",
            "Dá para alterar a dificuldade a meio?",
            "O que acharam da história?",
            "Drop de frames quando ataco",
            "Guia para speedrun",
            "O Atreus irrita-me!!!"), ContextCompat.getColor(applicationContext, R.color.gold20))
        recyclerView.layoutManager = LinearLayoutManager(this)

    }
}