package com.example.gamestate.ui.data.Forum

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestate.ui.data.TopicDC

class RecViewForumAdapter(private val mList: List<TopicDC>, private val gameID: Int, private var colorResource: Int) : RecyclerView.Adapter<RecViewHolderForumAdapter>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecViewHolderForumAdapter {
        val inflater = LayoutInflater.from(parent.context)
        return RecViewHolderForumAdapter(inflater, parent)
    }
    override fun onBindViewHolder(holder: RecViewHolderForumAdapter, position: Int) {
        val color = when(position % 2) {
            1 -> colorResource
            else -> 0
        }
        val id = mList[position].id
        val text = mList[position].name
        holder.bindData(id,gameID,text,color)
    }
    override fun getItemCount(): Int {
        return mList.size
    }
}