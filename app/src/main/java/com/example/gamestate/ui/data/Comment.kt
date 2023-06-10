package com.example.gamestate.ui.data

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Comment (
    val text: String,
    val username: String,
    val elapsedTime: String
)