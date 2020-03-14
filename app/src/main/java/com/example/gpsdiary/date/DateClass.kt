package com.example.gpsdiary.date

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.time.LocalDate

open class DateClass(
    @PrimaryKey var date : LocalDate
): RealmObject() {

}