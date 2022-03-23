package ru.get.hd.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = false)
    var id: Long,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "place")
    var place: String,

    @ColumnInfo(name = "date")
    var date: Long,

    @ColumnInfo(name = "time")
    var time: String,

    @ColumnInfo(name = "affirmationNumber")
    var affirmationNumber: Int,

    @ColumnInfo(name = "affirmationDayMills")
    var affirmationDayMills: Long,

    @ColumnInfo(name = "forecastNumber")
    var forecastNumber: Int,

    @ColumnInfo(name = "forecastWeekMills")
    var forecastWeekMills: Long,

    @ColumnInfo(name = "lat")
    var lat: String,

    @ColumnInfo(name = "lon")
    var lon: String,

    @ColumnInfo(name = "subtitle1Ru")
    var subtitle1Ru: String? = null,

    @ColumnInfo(name = "subtitle1En")
    var subtitle1En: String? = null,

    @ColumnInfo(name = "subtitle2")
    var subtitle2: String? = null,

    @ColumnInfo(name = "subtitle3Ru")
    var subtitle3Ru: String? = null,

    @ColumnInfo(name = "subtitle3En")
    var subtitle3En: String? = null

)