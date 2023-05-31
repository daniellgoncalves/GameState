package com.example.gamestate.ui.data.Home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestate.R

class RecViewHolderHomeAdapter(inflater: LayoutInflater, val parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_rv_home, parent, false)) {
    private var tv: TextView? = itemView.findViewById(R.id.layout_game_name_home)

    fun bindData(text: String, colorResource: Int) {
        tv?.text = text
        tv?.setBackgroundColor(colorResource)
        itemView.setOnClickListener {
            Toast.makeText(parent.context,text,Toast.LENGTH_LONG).show()
        }
    }
}