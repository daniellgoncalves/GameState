package com.example.gamestate.ui.data.Notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class RecViewNotificationAdapter(private val mUsernameList: List<String>,private val melapsedtimeList: List<String>, private val mImagesList: List<String>, private val mIDList: List<String>, private val mForumIDList:List<Int>) : RecyclerView.Adapter<RecViewHolderNotificationAdapter>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecViewHolderNotificationAdapter{
        val inflater = LayoutInflater.from(parent.context)
        return RecViewHolderNotificationAdapter(inflater, parent)
    }
    override fun onBindViewHolder(holder: RecViewHolderNotificationAdapter, position: Int) {
        val username = mUsernameList[position]
        val elapsedtime = melapsedtimeList[position]
        val image = mImagesList[position]
        val id = mIDList[position]
        val forumID = mForumIDList[position]
        holder.bindData(username,elapsedtime, image,id,forumID)
    }
    override fun getItemCount(): Int {
        return mUsernameList.size
    }
}