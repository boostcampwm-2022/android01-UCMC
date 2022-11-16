package com.gta.presentation.util

object DateUtil {
    private const val DAY_TIME_UNIT = 86400000L

    @JvmStatic
    fun getDateCount(startDate: Long, endDate: Long) =
        (endDate - startDate).div(DAY_TIME_UNIT).plus(1).toInt()
}
