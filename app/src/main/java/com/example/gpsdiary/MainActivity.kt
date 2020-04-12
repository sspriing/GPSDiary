package com.example.gpsdiary

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gpsdiary.date.DateActivity
import com.example.gpsdiary.date.DateAdapter
import com.example.gpsdiary.date.DateClass
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDate
import java.util.*


class MainActivity : AppCompatActivity() {

    val database : FirebaseFirestore = FirebaseFirestore.getInstance()
    val TAG = "MyMessage"
    val nownow: DateClass = DateClass(LocalDate.now().toString())
    var i = mutableListOf<DateClass>()
    lateinit var mAdapter : DateAdapter
    var record  = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getDate("User")
        //date에 들어있는 배열을 불러와서 RecyclerView에 보여줌

        mAdapter = DateAdapter(this, i)
        recycle_date.adapter = mAdapter
        recycle_date.layoutManager = LinearLayoutManager(this)


        button.setOnClickListener {
            createDate(nownow)
            mAdapter.addItem(nownow)
            if(record ==false){
                record =true
                recordLoc(nownow)
                button.setText("오늘 기록 중지")
            }
            else{
                record=false
                button.setText("오늘 기록 시작")
            }
        }

    }

    private fun createDate(date: DateClass){
        database.collection("GPSDiary")
            .document("User")
            .collection("Date")
            .document("${date.date}")
            .set(date)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot add with ID: ${documentReference}")
            }
            .addOnFailureListener{e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun getDate(name: String){
        setContentView(R.layout.activity_main)
        var date : DateClass
        database.collection("GPSDiary")
            .document(name)
            .collection("Date")
            .get()
            .addOnSuccessListener {result ->
                for(document in result){
                    Log.d(TAG, "${document.id}==>${document.data}Here")
                    date = DateClass(document.id)
                    mAdapter.addItem(date)
                }
            }
            .addOnFailureListener{exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    private fun recordLoc(date: DateClass){
        val recordLocation =RecordLocation(this, this, date)
        recordLocation.locationInit(this)
    }
}
