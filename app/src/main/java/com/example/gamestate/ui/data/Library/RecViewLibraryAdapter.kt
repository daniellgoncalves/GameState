package com.example.gamestate.ui.data.Library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestate.ui.data.Library.RecViewHolderLibraryAdapter

class RecViewLibraryAdapter(private val mList: List<String>, private val mImagesList: List<String>, private val mIDList: List<String>, private val mForumIDList: List<Int>) : RecyclerView.Adapter<RecViewHolderLibraryAdapter>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecViewHolderLibraryAdapter {
        val inflater = LayoutInflater.from(parent.context)
        return RecViewHolderLibraryAdapter(inflater, parent)
    }
    override fun onBindViewHolder(holder: RecViewHolderLibraryAdapter, position: Int) {
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