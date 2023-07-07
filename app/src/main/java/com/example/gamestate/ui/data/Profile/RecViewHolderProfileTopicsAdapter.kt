package com.example.gamestate.ui.data.Profile

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

class RecViewHolderProfileTopicsAdapter(inflater: LayoutInflater, val parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_rv_profile_topics, parent, false)) {
    private var tv: TextView? = itemView.findViewById(R.id.layout_text_library)
    private var iv: ImageView = itemView.findViewById(R.id.layout_image_library)

    fun bindData(text: String, image: String, id: String, forumID: Int) {
        tv?.text = text

        Glide.with(parent.context)
            .load(image)
            .centerCrop()
            .into(iv)

        itemView.setOnClickListener {
            val intent = Intent(parent.context, TopicActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("forum_id", forumID)
            Toast.makeText(parent.context, id, Toast.LENGTH_SHORT).show()
            parent.context.startActivity(intent)
        }
    }
}