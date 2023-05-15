package com.example.gamestate.ui.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.gamestate.R

class CustomAdapter(internal var context: Context, internal var images: IntArray, internal var settings: Array<String>) :
    BaseAdapter() {
    internal var inflter: LayoutInflater

    init {
        inflter = LayoutInflater.from(context)
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

        val view = inflter.inflate(R.layout.custom_layout_spinner,null)
        val icon = view.findViewById<View>(R.id.user_settings) as ImageView?
        val names = view.findViewById<View>(R.id.textView) as TextView?
        icon!!.setImageResource(images[i])
        names!!.text = settings[i]
        return view
    }
}