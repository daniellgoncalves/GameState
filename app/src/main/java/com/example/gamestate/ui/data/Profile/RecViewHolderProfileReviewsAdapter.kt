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

class RecViewHolderProfileReviewsAdapter(inflater: LayoutInflater, val parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_rv_profile, parent, false)) {
    private var title_tv: TextView? = itemView.findViewById(R.id.layout_title_profile)
    private var iv: ImageView = itemView.findViewById(R.id.layout_image_library)
    private var text_tv: TextView? = itemView.findViewById(R.id.layout_text)
    private var rating_tv: TextView? = itemView.findViewById(R.id.layout_rating_profile)

    fun bindData(rating: String, image: String, title: String, text: String) {
        title_tv?.text = title
        text_tv?.text = text
        rating_tv?.text = rating

        Glide.with(parent.context)
            .load(image)
            .centerCrop()
            .into(iv)
    }
}