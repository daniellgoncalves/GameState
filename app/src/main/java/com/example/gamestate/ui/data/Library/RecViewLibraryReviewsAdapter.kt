package com.example.gamestate.ui.data.Library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class RecViewLibraryReviewsAdapter(private val mRatingList: List<String>, private val mImagesList: List<String>, private val mTitleList: List<String>, private val mTextList: List<String>, private val mIDList: List<String>, private val mForumIDList: List<Int>) : RecyclerView.Adapter<RecViewHolderLibraryReviewsAdapter>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecViewHolderLibraryReviewsAdapter {
        val inflater = LayoutInflater.from(parent.context)
        return RecViewHolderLibraryReviewsAdapter(inflater, parent)
    }
    override fun onBindViewHolder(holder: RecViewHolderLibraryReviewsAdapter, position: Int) {
        val rating = mRatingList[position]
        val image = mImagesList[position]
        val title = mTitleList[position]
        val text = mTextList[position]
        val id = mIDList[position]
        val forumID = mForumIDList[position]
        holder.bindData(rating, image, title, text, id, forumID)
    }
    override fun getItemCount(): Int {
        return mRatingList.size
    }
}