package com.example.gpsdiary.spot

import android.app.Application
import io.realm.Realm

class GPSDiary: Application() {
    override fun onCreate(){
        super.onCreate()
        //데이터베이스 초기화
        Realm.init(this)
    }
}