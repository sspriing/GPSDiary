package com.example.gpsdiary

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.gpsdiary.date.DateActivity
import com.example.gpsdiary.date.DateClass
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton
import java.sql.Time
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RecordLocation(context: Context, activity: Activity, date_:DateClass): OnMapReadyCallback {
    val date = date_
    val database : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallBack: MyLocationCallBack

    private val REQUEST_ACCESS_FINE_LOCATION =1000

    init{
        locationInit(context)
    }
    fun locationInit(context: Context){
        fusedLocationProviderClient = FusedLocationProviderClient(context)
        locationCallBack = MyLocationCallBack()
        locationRequest = LocationRequest()
        //위치 정확성 요구정도. PRIORITY_HIGH_ACCURACY: 가장 정확한 위치를 요청.
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        //위치정보 요구하는 시간간격
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
    }
    override fun onResume(){
        super.onResume()
        addLocationListenr()

        permissionCheck(cancle = {showPermissionInfoDialog()}, ok = {addLocationListener()})
    }

    inner class MyLocationCallBack: LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            val location = locationResult?.lastLocation


            location?.run{
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ISO_LOCAL_TIME
                val formatted = current.format(formatter)
                val gPoint  = GeoPoint(latitude, longitude)
                val spot = hashMapOf(
                    formatted to gPoint
                )
                Log.d("MyMessage", gPoint.toString())

                database.collection("GPSDiary")
                    .document("User")
                    .collection("Date")
                    .document("${date.date}")
                    .set(spot)
            }
            }

        }
    }



