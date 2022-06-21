package com.myhumandesignhd.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.myhumandesignhd.App
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "children")
data class Child(
    @PrimaryKey(autoGenerate = false)
    var id: Long,

    @ColumnInfo(name = "parentId")
    var parentId: Long,

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
    var subtitle3En: String? = null,

    @ColumnInfo(name = "kidDescriptionRu")
    var kidDescriptionRu: String? = null,

    @ColumnInfo(name = "kidDescriptionEn")
    var kidDescriptionEn: String? = null,

    @ColumnInfo(name = "titles")
    @TypeConverters(Converters::class)
    var titles: List<String>? = null,

    @ColumnInfo(name = "descriptions")
    @TypeConverters(Converters::class)
    var descriptions: List<String>? = null
)

fun Child.getDateStr(): String {
    val hours = time.split(":")[0].toInt()
    val minutes = time.split(":")[1].toInt()

    val formatter: DateFormat = SimpleDateFormat(App.DATE_FORMAT, Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("GMT")

    val cal = GregorianCalendar(TimeZone.getTimeZone("GMT"))

    cal.timeInMillis =
        (date / 86400000).toInt() * 86400000L + hours * 3600000L + minutes * 60000L

    return formatter.format(cal.time)
}

class Converters {
    @TypeConverter
    fun fromListToJson(media: List<String>?): String? =
        Gson().toJson(media)

    @TypeConverter
    fun fromJsonToList(json: String?): List<String>? {
        if (json.isNullOrEmpty())
            return emptyList()
        return Gson().fromJson(json, object : TypeToken<List<String>>() {}.type)
    }
}