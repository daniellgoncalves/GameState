package com.example.gamestate.ui.data.Profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestate.ui.data.Library.RecViewHolderLibraryAdapter

class RecViewProfileTopicsAdapter(private val mList: List<String>, private val mImagesList: List<String>, private val mIDList: List<String>, private val mForumIDList: List<Int>) : RecyclerView.Adapter<RecViewHolderProfileTopicsAdapter>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecViewHolderProfileTopicsAdapter {
        val inflater = LayoutInflater.from(parent.context)
        return RecViewHolderProfileTopicsAdapter(inflater, parent)
    }
    override fun onBindViewHolder(holder: RecViewHolderProfileTopicsAdapter, position: Int) {
        val text = mList[position]
        val image = mImagesList[position]
        val id = mIDList[position]
        val forumID = mForumIDList[position]
        holder.bindData(text, image, id, forumID)
    }
    override fun getItemCount(): Int {
        return mList.size
    }
}