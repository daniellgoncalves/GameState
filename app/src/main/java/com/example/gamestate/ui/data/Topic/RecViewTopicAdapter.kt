package com.example.gamestate.ui.data.Topic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestate.ui.data.Topic.RecViewHolderTopicAdapter
import com.example.gamestate.ui.data.Comment

class RecViewTopicAdapter(private val mList: List<Comment>) : RecyclerView.Adapter<RecViewHolderTopicAdapter>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecViewHolderTopicAdapter {
        val inflater = LayoutInflater.from(parent.context)
        return RecViewHolderTopicAdapter(inflater, parent)
    }
    override fun onBindViewHolder(holder: RecViewHolderTopicAdapter, position: Int) {
        val comment = mList[position]
        holder.bindData(comment.text, comment.username, comment.elapsedTime)
    }
    override fun getItemCount(): Int {
        return mList.size
    }
}