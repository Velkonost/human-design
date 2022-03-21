package ru.get.hd.ui.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.NumberPicker
import ru.get.hd.ui.view.datepicker.view.datePicker.DatePicker
import java.lang.reflect.Field


internal class CustomDatePicker(context: Context?, attrs: AttributeSet?) :
    DatePicker(context, attrs) {
    init {
        val fields: Array<Field> = DatePicker::class.java.declaredFields
        try {
            val s = arrayOf(
                "Januar123y",
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
                field.isAccessible = true
                if (TextUtils.equals(field.name, "mMonthSpinner")) {
                    val monthPicker = field.get(this) as NumberPicker
                    monthPicker.minValue = 0
                    monthPicker.maxValue = 11
                    monthPicker.displayedValues = s
                }
                if (TextUtils.equals(field.name, "mShortMonths")) {
                    field.set(this, s)
                }
            }
        } catch (e: Exception) {
            println(e.message)
            e.printStackTrace()
        }
    }


}