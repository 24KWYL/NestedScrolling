package com.wyl.nestedscrolling

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class RecyclerViewAdapter(var context: Context, var dataList: ArrayList<Data>) :
    RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.MyViewHolder {
        return MyViewHolder(layoutInflater.inflate(R.layout.layout_item, parent, false))
    }

    override fun getItemCount(): Int {
        return if (dataList == null) 0 else dataList.size
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.MyViewHolder, position: Int) {
        holder.tv?.text = "NestedScrolling$position"
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tv: TextView? = null

        init {
            tv = itemView.findViewById(R.id.tv)
        }
    }
}