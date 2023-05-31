package com.example.gamestate.ui.data.Forum

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.gamestate.R

class SpinnerForumAdapter(internal var context: Context, internal var images: IntArray, internal var settings: Array<String>) :
    BaseAdapter() {
    internal var inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun getItem(i: Int): Any? {
        return null
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {

        val view = inflater.inflate(R.layout.layout_spinner_forum,null)
        val icon = view.findViewById<View>(R.id.spinner_icon) as ImageView?
        val names = view.findViewById<View>(R.id.spinner_textView) as TextView?
        icon!!.setImageResource(images[i])
        names!!.text = settings[i]
        return view
    }
}