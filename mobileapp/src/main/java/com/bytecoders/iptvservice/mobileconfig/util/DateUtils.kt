package com.bytecoders.iptvservice.mobileconfig.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val dateFormat: SimpleDateFormat by lazy { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US) }
    private val timeFormat: SimpleDateFormat by lazy { SimpleDateFormat("HH:mm", Locale.US) }

    @JvmStatic
    fun getDateString(timestamp: Long): String = dateFormat.format(Date(timestamp))

    @JvmStatic
    fun getTimeString(timestamp: Long): String = timeFormat.format(Date(timestamp))
}