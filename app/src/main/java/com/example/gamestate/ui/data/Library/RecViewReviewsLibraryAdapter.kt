package com.example.gamestate.ui.data.Library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class RecViewReviewsLibraryAdapter(private val mListtitle: List<String>, private val mImagesList: List<String>) : RecyclerView.Adapter<RecViewReviewsLibraryHolderAdapter>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecViewReviewsLibraryHolderAdapter {
        val inflater = LayoutInflater.from(parent.context)
        return RecViewReviewsLibraryHolderAdapter(inflater, parent)
    }
    override fun onBindViewHolder(holder: RecViewReviewsLibraryHolderAdapter, position: Int) {
        val texttile =  mListtitle[position]
        val image = mImagesList[position]
        holder.bindData(texttile,image)
    }
    override fun getItemCount(): Int {
        return mListtitle.size
    }
}