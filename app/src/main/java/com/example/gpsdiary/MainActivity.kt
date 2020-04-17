package com.example.gpsdiary

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gpsdiary.date.DateActivity
import com.example.gpsdiary.date.DateAdapter
import com.example.gpsdiary.date.DateClass
import com.example.gpsdiary.spot.LocationClass
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity() {

    val database : FirebaseFirestore = FirebaseFirestore.getInstance()
    val TAG = "MyMessage"
    val nownow: DateClass = DateClass(LocalDate.now().toString())
    var i = mutableListOf<DateClass>()
    lateinit var mAdapter : DateAdapter
    var record  = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: MainActivity.MyLocationCallBack

    private val REQUEST_ACCESS_FINE_LOCATION = 1000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getDate("User")
        //date에 들어있는 배열을 불러와서 RecyclerView에 보여줌

        mAdapter = DateAdapter(this, i)
        recycle_date.adapter = mAdapter
        recycle_date.layoutManager = LinearLayoutManager(this)
        recordLoc(nownow)

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
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        locationCallback = MyLocationCallBack()
        locationRequest = LocationRequest()
        //위치 정확성 요구정도. PRIORITY_HIGH_ACCURACY: 가장 정확한 위치를 요청.
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        //위치정보 요구하는 시간간격
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
    }
    //위치요청메소드 실행
    override fun onResume(){
        super.onResume()
        addLocationListener()

        permissionCheck(cancel = {showPermissionInfoDialog()}, ok = {addLocationListener()})
    }
    //위치요청
    @SuppressLint("MissingPermission")
    private fun addLocationListener(){
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    //사용자의 위치정보가 null이 아닐떄 해당 위치로 카메라 이동, 위도 경도를 로그에 표시
    inner class MyLocationCallBack: LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult?){
            super.onLocationResult(locationResult)

            val location = locationResult?.lastLocation

            location?.run{
                var latlng = LocationClass(location.latitude, location.longitude)

                var current = LocalDateTime.now()
                var formatter = DateTimeFormatter.ofPattern("HH : mm : ss ")
                var formatted = current.format(formatter)
                var doc = hashMapOf(formatted to latlng)

                database.collection("GPSDiary")
                    .document("User")
                    .collection("Date")
                    .document("${nownow.date}")
                    .collection("Location")
                    .document("$formatted")
                    .set(latlng)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot add with ID: ${documentReference}")
                    }
                    .addOnFailureListener{e ->
                        Log.w(TAG, "Error adding document", e)
                    }
            }
        }
    }

    //권한요청
    private fun permissionCheck(cancel: ()->Unit, ok: () -> Unit){
        //권한승인이 되지 않았을때
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            //권한승인 요청을 한번 거부당한 경우
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                cancel()
            }else{
                //권환요청 진행
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_ACCESS_FINE_LOCATION)
            }
        }else{  //권환이 승인되어있을때
            ok()
        }

    }

    private fun showPermissionInfoDialog(){
        alert("현재 위치정보를 얻으려면 위치권환이 필요합니다.", "권한이 필요한 이유"){
            yesButton {
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_ACCESS_FINE_LOCATION)
            }
            noButton{}
        }.show()
    }

    //권한요청 처리
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,grantResults: IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
            REQUEST_ACCESS_FINE_LOCATION -> {
                if((grantResults.isNotEmpty())&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    addLocationListener()
                }else{
                    toast("권한 거부됨")
                }
                return
            }
        }
    }

    //위치정보요청 삭제
    override fun onPause(){
        super.onPause()
        removeLocationListener()
    }
    private fun removeLocationListener(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


}
