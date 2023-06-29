package com.example.gamestate.ui.data.Game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class RecViewReviewsGameAdapter(private val mListtitle: List<String>, private val mListrating: List<Int>) : RecyclerView.Adapter<RecViewReviewsGameHolderAdapter>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecViewReviewsGameHolderAdapter {
        val inflater = LayoutInflater.from(parent.context)
        return RecViewReviewsGameHolderAdapter(inflater, parent)
    }
    override fun onBindViewHolder(holder: RecViewReviewsGameHolderAdapter, position: Int) {
        val texttile =  mListtitle[position]
        val rating = mListrating[position]
        holder.bindData(texttile,rating)
    }
    override fun getItemCount(): Int {
        return mListtitle.size
    }
}