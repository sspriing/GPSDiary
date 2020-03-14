package com.example.gpsdiary

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gpsdiary.date.DateAdapter
import com.example.gpsdiary.date.DateClass
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDate


class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //현재시간을 MutableList로 저장 (임시)
        val now : LocalDate = LocalDate.now()
        val nownow: DateClass =
            DateClass(now)
        var i = mutableListOf<DateClass>(nownow, nownow)


        //date에 들어있는 배열을 불러와서 RecyclerView에 보여줌
        recycle_date.adapter = DateAdapter(this, i)
        recycle_date.layoutManager = LinearLayoutManager(this)
    }

}
