package com.bytecoders.iptvservice.mobileconfig.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val dateFormat: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US)

    @JvmStatic
    fun getDateString(timestamp: Long): String = dateFormat.format(Date(timestamp))
}