package com.gta.presentation.util

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

object DateUtil {
    private const val DAY_TIME_UNIT = 60 * 60 * 24 * 1000L

    val dateFormat = SimpleDateFormat("yy/MM/dd", Locale.getDefault())

    fun getDateCount(startDate: Long, endDate: Long) =
        abs(endDate - startDate).div(DAY_TIME_UNIT).plus(1).toInt()
}
