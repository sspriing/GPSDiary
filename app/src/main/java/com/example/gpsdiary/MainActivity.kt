package com.example.gpsdiary

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDate


class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val now : LocalDate = LocalDate.now()
        val nownow: DateClass = DateClass(now)
        var i = mutableListOf<DateClass>(nownow, nownow)

        recycle_date.adapter = DateAdapter(i)
        recycle_date.layoutManager = LinearLayoutManager(this)
    }


}
