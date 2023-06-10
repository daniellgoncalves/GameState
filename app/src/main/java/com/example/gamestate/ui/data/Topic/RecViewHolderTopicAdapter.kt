package com.example.gamestate.ui.data.Topic

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamestate.R
import com.example.gamestate.ui.TopicActivity

class RecViewHolderTopicAdapter(inflater: LayoutInflater, val parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_rv_topic, parent, false)) {
    private var tvText: TextView? = itemView.findViewById(R.id.layout_text_topic)
    private var tvUsername: TextView? = itemView.findViewById(R.id.layout_username_topic)
    private var tvElapsedTime: TextView? = itemView.findViewById(R.id.layout_time_topic)

    fun bindData(text: String, username: String, elapsedTime: String) {
        tvText?.text = text
        tvUsername?.text = username
        tvElapsedTime?.text = elapsedTime

        /*itemView.setOnClickListener {

        }*/
    }
}