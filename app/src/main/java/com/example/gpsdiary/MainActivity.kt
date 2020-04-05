package com.example.gpsdiary

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gpsdiary.date.DateAdapter
import com.example.gpsdiary.date.DateClass
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDate


class MainActivity : AppCompatActivity() {

    val database : FirebaseFirestore = FirebaseFirestore.getInstance()
    val TAG = "MyMessage"
    //현재시간을 MutableList로 저장 (임시)
    val now : LocalDate = LocalDate.now()
    val nownow: DateClass =
        DateClass(now.toString())
    var i = mutableListOf<DateClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getDate("User")

        //date에 들어있는 배열을 불러와서 RecyclerView에 보여줌
        recycle_date.adapter = DateAdapter(this, i)
        recycle_date.layoutManager = LinearLayoutManager(this)

        button.setOnClickListener {
            createDate(nownow)
            getDate("User")
        }

    }

    private fun createUser(name: String){
        val user = hashMapOf(
            "userName" to name,
            "userID" to name
        )

        database.collection("GPSDiary")
             .document()
             .set(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot add with ID: ${documentReference}")
            }
            .addOnFailureListener{e->
                Log.w(TAG, "Error adding document", e)
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
        database.collection("GPSDiary")
            .document("User")
            .collection("date")
            .get()
            .addOnSuccessListener {result ->
                for(document in result){
                    Log.d(TAG, "${document.id}==>${document.data}Here")
                    i.add(DateClass(document.data.toString()))
                }
            }
            .addOnFailureListener{exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    private fun getInfo(name: String){
        database?.collection("User").whereEqualTo("userName","Bomi").get()
            .addOnSuccessListener {result ->
                for(document in result){
                    Log.d(TAG, "${document.id}==>${document.data}")

                }
            }
            .addOnFailureListener{exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }


}
