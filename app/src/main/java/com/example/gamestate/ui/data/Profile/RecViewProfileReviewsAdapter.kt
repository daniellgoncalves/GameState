package com.example.gamestate.ui.data.Profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestate.ui.data.Library.RecViewHolderLibraryAdapter

class RecViewProfileReviewsAdapter(private val mRatingList: List<String>, private val mImagesList: List<String>, private val mTitleList: List<String>, private val mTextList: List<String>) : RecyclerView.Adapter<RecViewHolderProfileReviewsAdapter>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecViewHolderProfileReviewsAdapter {
        val inflater = LayoutInflater.from(parent.context)
        return RecViewHolderProfileReviewsAdapter(inflater, parent)
    }
    override fun onBindViewHolder(holder: RecViewHolderProfileReviewsAdapter, position: Int) {
        val rating = mRatingList[position]
        val image = mImagesList[position]
        val title = mTitleList[position]
        val text = mTextList[position]
        holder.bindData(rating, image, title, text)
    }
    override fun getItemCount(): Int {
        return mRatingList.size
    }
}