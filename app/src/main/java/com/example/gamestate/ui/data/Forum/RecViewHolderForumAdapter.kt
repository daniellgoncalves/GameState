package com.example.gamestate.ui.data.Forum

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestate.R
import com.example.gamestate.ui.TopicActivity

class RecViewHolderForumAdapter(inflater: LayoutInflater, val parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_rv_forum, parent, false)) {
    private var tv: TextView? = itemView.findViewById(R.id.layout_game_name_forum)

    fun bindData(id: String, gameID: Int, text: String, colorResource: Int) {
        tv?.text = text
        tv?.setBackgroundColor(colorResource)

        itemView.setOnClickListener {
            val intent = Intent(parent.context, TopicActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("forum_id", gameID)
            Toast.makeText(parent.context, id, Toast.LENGTH_SHORT).show()
            parent.context.startActivity(intent)
        }
    }
}