package com.example.gamestate.ui.data.Game

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestate.R

class RecViewHolderGameAdapter(inflater: LayoutInflater, val parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_rv_game, parent, false)) {
    private var tv: TextView? = itemView.findViewById(R.id.layout_platforms_game)

    fun bindData(text: String) {
        tv?.text = text
    }
}