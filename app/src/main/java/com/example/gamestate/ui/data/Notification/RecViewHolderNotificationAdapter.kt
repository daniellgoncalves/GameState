package com.example.gamestate.ui.data.Notification

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

class RecViewHolderNotificationAdapter(inflater: LayoutInflater, val parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_rv_notification, parent, false)) {
    private var tvUsername: TextView? = itemView.findViewById(R.id.layout_text_notification)
    private var tvElapsedTime: TextView? = itemView.findViewById(R.id.layout_elapsedtime_notification)
    private var iv: ImageView = itemView.findViewById(R.id.layout_image_notification)

    fun bindData(username: String, elapsedTime:String, image: String,id: String, forumID: Int) {
        tvUsername?.text = username
        tvElapsedTime?.text = elapsedTime

        Glide.with(parent.context)
            .load(image)
            .centerCrop()
            .into(iv)
    }
}