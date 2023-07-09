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
import com.example.gamestate.ui.GameActivity
import com.example.gamestate.ui.TopicActivity

class RecViewHolderProfileWishlistAdapter(inflater: LayoutInflater, val parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_rv_profile_wishlist, parent, false)) {
    private var name_tv: TextView? = itemView.findViewById(R.id.layout_text_wishlist)
    private var iv: ImageView = itemView.findViewById(R.id.layout_image_library)

    fun bindData(name: String, image: String, gameID: Int) {
        name_tv?.text = name

        Glide.with(parent.context)
            .load(image)
            .centerCrop()
            .into(iv)

        itemView.setOnClickListener {
            val intent = Intent(parent.context, GameActivity::class.java)
            intent.putExtra("id", gameID)
            Toast.makeText(parent.context, gameID, Toast.LENGTH_SHORT).show()
            parent.context.startActivity(intent)
        }
    }
}