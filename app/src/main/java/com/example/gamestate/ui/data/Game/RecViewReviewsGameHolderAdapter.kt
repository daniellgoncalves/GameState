package com.example.gamestate.ui.data.Game

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamestate.R

class RecViewReviewsGameHolderAdapter(inflater: LayoutInflater, val parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_rv_reviews_game, parent, false)) {
    private var im: ImageView = itemView.findViewById(R.id.layout_image_user)
    private var title: TextView? = itemView.findViewById(R.id.layout_text_title)
    private var rating: TextView? = itemView.findViewById(R.id.ratingreview)

    fun bindData(texttitle: String,rat: Int) {
        title?.text = texttitle
        rating?.text = rat.toString()
    }
}