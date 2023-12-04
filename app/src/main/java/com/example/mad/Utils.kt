package com.example.mad

import java.text.SimpleDateFormat
import java.util.*

class Utils {
    companion object {
        // Converts a Date object into a String using dd MMM yyyy format (e.g. 12 Sep 2019)
        fun dateToString(date: Date): String =
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)

        // Converts a day, month and year into a Date object.
        fun dayMonthYearToDate(day: String, month: String, year: String): Date? {
            val dateString = StringBuilder()
            dateString
                .append(day)
                .append(month)
                .append(year)

            val format = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
            return format.parse(dateString.toString())
        }
    }
}