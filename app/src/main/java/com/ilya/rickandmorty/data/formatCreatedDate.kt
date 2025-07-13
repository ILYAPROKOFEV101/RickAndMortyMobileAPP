package com.ilya.rickandmorty.data

import android.os.Build
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

fun String.formatCreatedDate(): String {
    return try {
        val inputFormatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter.ISO_OFFSET_DATE_TIME
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
        val date = OffsetDateTime.parse(this, inputFormatter)
        outputFormatter.format(date)
    } catch (e: Exception) {
        this
    }
}
