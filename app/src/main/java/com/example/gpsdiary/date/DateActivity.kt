package com.example.gpsdiary.date

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.gpsdiary.MainActivity
import com.example.gpsdiary.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.io.FileInputStream
import java.io.FileOutputStream

class DateActivity : AppCompatActivity(), OnMapReadyCallback {
    //위치정보 기록을 위한 변수
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: MyLocationCallBack

    private val REQUEST_ACCESS_FINE_LOCATION = 1000
    //Polyline 옵션
    private val polylineOptions = PolylineOptions().width(5f).color(Color.RED)

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.route_main)


        //route_main.xml 의 mapView Fragment에 구글지도 연동
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationInit()

    }

    override fun onMapReady(googleMap: GoogleMap){
        mMap = googleMap
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    //선언된 위치관련 변수들 초기화
    private fun locationInit(){
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
                val latLng = LatLng(latitude, longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
                //로그에 위도경도 저장
                Log.d("DataActivity", "위도: $latitude, 경도: $longitude")

                //PolyLine에 좌표추가
                polylineOptions.add(latLng)
                //선그리기
                mMap.addPolyline(polylineOptions)

//                val p = GPXParser()
//                val gpx = GPX()
//                val out = FileOutputStream("outFile.gpx")
//                p.writeGPX(gpx, out)
//                out.close()
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
                ActivityCompat.requestPermissions(this@DateActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_ACCESS_FINE_LOCATION)
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