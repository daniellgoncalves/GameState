package com.example.gamestate.ui.data.Game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class RecViewGameAdapter(private val mList: List<String>) : RecyclerView.Adapter<RecViewHolderGameAdapter>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecViewHolderGameAdapter {
        val inflater = LayoutInflater.from(parent.context)
        return RecViewHolderGameAdapter(inflater, parent)
    }
    override fun onBindViewHolder(holder: RecViewHolderGameAdapter, position: Int) {
        val text = mList[position]
        holder.bindData(text)
    }
    override fun getItemCount(): Int {
        return mList.size
    }
}