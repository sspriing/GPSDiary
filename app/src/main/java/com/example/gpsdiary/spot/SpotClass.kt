package com.example.gpsdiary.spot

import io.realm.annotations.PrimaryKey
import java.sql.Time
import java.time.LocalDate

open class SpotClass(
    @PrimaryKey var id: Int = 0,
    var time: Time,
    var location: LocationClass,
    var date: LocalDate
) {

}