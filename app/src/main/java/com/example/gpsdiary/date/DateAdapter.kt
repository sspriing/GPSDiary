package com.example.gpsdiary.date

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gpsdiary.R
import kotlinx.android.synthetic.main.date_item.view.*

class DateAdapter(context: Context, dates: MutableList<DateClass>): RecyclerView.Adapter<DateAdapter.ViewHolder>() {

    var dates = dates
    val context = context

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(parent)


    override fun getItemCount(): Int  = dates.size
    fun addItem(date: DateClass){
        if(dates.contains(date)==false){
            dates.add(date)
            notifyDataSetChanged()
        }
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        dates[position].let{
            item->
            with(holder){
                date.text = item.date.toString()
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