package com.example.gpsdiary.spot

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gpsdiary.R
import com.example.gpsdiary.date.DateActivity
import kotlinx.android.synthetic.main.date_item.view.*

class SpotAdapter(context: Context, spots: MutableList<SpotClass>): RecyclerView.Adapter<SpotAdapter.ViewHolder>() {

    var spots = spots
    val context = context

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(parent)


    override fun getItemCount(): Int  = spots.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        spots[position].let{
                item->
            with(holder){
                date.text = item.latitude.toString()
            }
        }

        holder.itemView.setOnClickListener {
            val nextIntent = Intent(context, DateActivity::class.java)
            context.startActivity(nextIntent)
        }
    }

    inner class ViewHolder(parent: ViewGroup):RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.date_item, parent, false)){
        val date = itemView.date_title
    }
}