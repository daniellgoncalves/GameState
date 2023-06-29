package com.example.gamestate.ui.data.Library

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamestate.R

class RecViewReviewsLibraryHolderAdapter (inflater: LayoutInflater, val parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_rv_reviews_library, parent, false)) {
    private var im: ImageView = itemView.findViewById(R.id.layout_image_reviews_library)
    private var title: TextView? = itemView.findViewById(R.id.layout_text_title)

    fun bindData(texttitle: String,image: String) {
        title?.text = texttitle
        Glide.with(parent.context)
            .load(image)
            .centerCrop()
            .into(im)
    }
}