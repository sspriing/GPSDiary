package com.example.gpsdiary.spot

import io.realm.annotations.PrimaryKey
import java.time.LocalDate

open class RouteClass (
    @PrimaryKey var date: LocalDate,
    var route: MutableList<LocationClass>,
    var a: LocationClass
)