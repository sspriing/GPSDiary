package com.example.gpsdiary

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gpsdiary.date.DateAdapter
import com.example.gpsdiary.date.DateClass
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDate


class MainActivity : AppCompatActivity() {

    val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    val myRef : DatabaseReference = database.getReference("userID")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //현재시간을 MutableList로 저장 (임시)
        val now : LocalDate = LocalDate.now()
        val nownow: DateClass =
            DateClass(now.toString())
        var i = mutableListOf<DateClass>(nownow)


        //date에 들어있는 배열을 불러와서 RecyclerView에 보여줌
        recycle_date.adapter = DateAdapter(this, i)
        recycle_date.layoutManager = LinearLayoutManager(this)

        button.setOnClickListener {
            myRef.child("userID").push().setValue("Me")
            myRef.child("User").child("date").push().setValue(LocalDate.now().toString())
        }

    }


}
