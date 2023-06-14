package com.example.gamestate.ui.data.Home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class RecViewHomeAdapter(private val mList: List<String>, private var colorResource: Int,private val gameIDList: List<Int>) :
    RecyclerView.Adapter<RecViewHolderHomeAdapter>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecViewHolderHomeAdapter {
        val inflater = LayoutInflater.from(parent.context)
        return RecViewHolderHomeAdapter(inflater, parent)
    }
    override fun onBindViewHolder(holder: RecViewHolderHomeAdapter, position: Int) {
        val color = when(position % 2) {
            1 -> colorResource
            else -> 0
        }
        val text = mList[position]
        val id =  gameIDList[position]
        holder.bindData(text,color,id)
    }
    override fun getItemCount(): Int {
        return mList.size
    }
}