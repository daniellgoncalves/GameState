package com.example.gamestate.ui.data.Forum

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class RecViewForumAdapter(private val mList: List<String>, private var colorResource: Int) : RecyclerView.Adapter<RecViewHolderForumAdapter>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecViewHolderForumAdapter {
        val inflater = LayoutInflater.from(parent.context)
        return RecViewHolderForumAdapter(inflater, parent)
    }
    override fun onBindViewHolder(holder: RecViewHolderForumAdapter, position: Int) {
        val color = when(position % 2) {
            1 -> colorResource
            else -> 0
        }
        val text = mList[position]
        holder.bindData(text,color)
    }
    override fun getItemCount(): Int {
        return mList.size
    }
}