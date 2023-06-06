package com.example.gamestate.ui.data.Library

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamestate.R

class RecViewHolderLibraryAdapter(inflater: LayoutInflater, val parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_rv_library, parent, false)) {
    private var tv: TextView? = itemView.findViewById(R.id.layout_text_library)
    private var iv: ImageView = itemView.findViewById(R.id.layout_image_library)

    fun bindData(text: String, image: String) {
        tv?.text = text

        Glide.with(itemView)
            .load(image)
            .centerCrop()
            .into(iv)

        itemView.setOnClickListener {
            Toast.makeText(parent.context,text,Toast.LENGTH_LONG).show()
        }
    }
}