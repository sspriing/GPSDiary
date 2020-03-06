package com.example.gpsdiary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.date_item.view.*

class DateAdapter(dates: MutableList<DateClass>): RecyclerView.Adapter<DateAdapter.ViewHolder>() {

    var dates = dates

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(parent)


    override fun getItemCount(): Int  = dates.size
    override fun onBindViewHolder(holder: DateAdapter.ViewHolder, position: Int) {
        dates[position].let{
            item->
            with(holder){
                date.text = item.date.toString()
            }
        }

        holder.itemView.setOnClickListener {
            println("Clicked: ${dates.get(position).date}")
        }
    }

    inner class ViewHolder(parent: ViewGroup):RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.date_item, parent, false)){
        val date = itemView.date_title
    }
}