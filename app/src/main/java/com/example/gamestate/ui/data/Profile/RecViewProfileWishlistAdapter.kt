package com.example.gamestate.ui.data.Profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestate.ui.data.Library.RecViewHolderLibraryAdapter

class RecViewProfileWishlistAdapter(private val mNameList: List<String>, private val mImagesList: List<String>, private val mGameIDList: List<Int>) : RecyclerView.Adapter<RecViewHolderProfileWishlistAdapter>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecViewHolderProfileWishlistAdapter {
        val inflater = LayoutInflater.from(parent.context)
        return RecViewHolderProfileWishlistAdapter(inflater, parent)
    }
    override fun onBindViewHolder(holder: RecViewHolderProfileWishlistAdapter, position: Int) {
        val name = mNameList[position]
        val image = mImagesList[position]
        val gameID = mGameIDList[position]
        holder.bindData(name, image, gameID)
    }
    override fun getItemCount(): Int {
        return mGameIDList.size
    }
}