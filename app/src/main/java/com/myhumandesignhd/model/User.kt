package com.myhumandesignhd.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.myhumandesignhd.App
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor

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
    var subtitle3En: String? = null,

    @ColumnInfo(name = "parentDescription")
    var parentDescription: String? = null,

    @ColumnInfo(name = "injuryDateStart")
    var injuryDateStart: Long? = null,

    @ColumnInfo(name = "injuryGenerationDuration")
    var injuryGenerationDuration: Long? = null,

    @ColumnInfo(name = "isInjuryGenerated")
    var isInjuryGenerated: Boolean = false,

    @ColumnInfo(name = "affirmationsIdsList")
    @TypeConverters(Converters::class)
    var affirmationsIdsList: List<String>? = null,

    @ColumnInfo(name = "pushForecastIdsList")
    @TypeConverters(Converters::class)
    var pushForecastIdsList: List<String>? = null,

    @ColumnInfo(name = "pushForecastPosition")
    var pushForecastPosition: Int = 0,



) {
    @Ignore
    var compatibilityAvg: Int = 0
}

fun User.getDateStr(): String {
    val hours = time.split(":")[0]//.toInt()
    val minutes = time.split(":")[1]//.toInt()

    val formatter: DateFormat = SimpleDateFormat(App.DATE_FORMAT_SHORT, Locale.getDefault())
//    formatter.timeZone = TimeZone.getTimeZone("GMT")

    val cal = Calendar.getInstance()//GregorianCalendar(TimeZone.getTimeZone("GMT"))

    val localDataFormatter = DateTimeFormatter.ofPattern(App.DATE_FORMAT_SHORT)
    val localDate: String = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate().format(localDataFormatter)

    val days =
        if (date < 0) floor(date.toDouble() / 86400000)
        else floor(date.toDouble() / 86400000).toInt()

    cal.timeInMillis =
        (days.toLong()) * 86400000L// + hours * 3600000L + minutes * 60000L

    return "$localDate $hours:$minutes:00"
}