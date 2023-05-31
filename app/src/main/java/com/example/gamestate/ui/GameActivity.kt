package com.example.gamestate.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.gamestate.R

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val reviewButton = findViewById<Button>(R.id.review_button)
        val forumButton = findViewById<Button>(R.id.forum_button)

        reviewButton.setOnClickListener {
            startActivity(Intent(this, AddGameActivity::class.java))
        }

        forumButton.setOnClickListener {
            startActivity(Intent(this, ForumActivity::class.java))
        }
    }
}