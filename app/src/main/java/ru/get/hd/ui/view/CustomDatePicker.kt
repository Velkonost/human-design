package ru.get.hd.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.DatePicker
import android.text.TextUtils

import android.widget.NumberPicker
import java.lang.Exception
import java.lang.reflect.Field
import java.util.*

internal class CustomDatePicker(context: Context?, attrs: AttributeSet?) :
    DatePicker(context, attrs) {



    init {

        val fields: Array<Field> = DatePicker::class.java.declaredFields

        try {
            val s = arrayOf(
                "January",
                "February",
                "March",
                "April",
                "May",
                "June",
                "July",
                "August",
                "September",
                "October",
                "November",
                "December"
            )
            for (field in fields) {

                if (TextUtils.equals(field.name, "mDelegate")) {
//                    val delegate = this.AbstractDatePickerDelegate()
                }

                field.setAccessible(true)
                if (TextUtils.equals(field.getName(), "mMonthSpinner")) {
                    val monthPicker = field.get(this) as NumberPicker
                    monthPicker.minValue = 0
                    monthPicker.maxValue = 11
                    monthPicker.displayedValues = s
                }
                if (TextUtils.equals(field.getName(), "mShortMonths")) {
                    field.set(this, s)
                }
            }
        } catch (e: Exception) {
            println(e.message)
            e.printStackTrace()
        }
    }
}