package com.akimov.mobilebank

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val throwable: Throwable) : Result<Nothing>()
}

fun formatDate(dateString: String): String {
    // Parse the date string into a LocalDateTime object
    val dateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)

    // Format the LocalDateTime object to get the day of the month and month name
    val dayOfMonth = dateTime.dayOfMonth
    val monthName = dateTime.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)

    // Format the result as "dayOfMonth monthName"
    return "$dayOfMonth $monthName"
}