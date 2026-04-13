package com.josetoanto.subastas.core.utils

import android.os.Build
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

fun parseIsoToEpochMillisOrNull(value: String): Long? {
    if (value.isBlank()) return null

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        runCatching { Instant.parse(value).toEpochMilli() }.getOrNull()?.let { return it }
        runCatching { OffsetDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME).toInstant().toEpochMilli() }
            .getOrNull()
            ?.let { return it }
        runCatching {
            LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        }.getOrNull()?.let { return it }
        return null
    }

    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSX",
        "yyyy-MM-dd'T'HH:mm:ssX",
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ss"
    )

    for (pattern in patterns) {
        val sdf = SimpleDateFormat(pattern, Locale.US).apply {
            isLenient = false
            timeZone = if (pattern.contains("X") || pattern.contains("'Z'")) {
                TimeZone.getTimeZone("UTC")
            } else {
                TimeZone.getDefault()
            }
        }
        runCatching { sdf.parse(value)?.time }.getOrNull()?.let { return it }
    }
    return null
}