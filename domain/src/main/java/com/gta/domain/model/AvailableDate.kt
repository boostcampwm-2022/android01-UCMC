package com.gta.domain.model

data class AvailableDate(
    val start: Long = 0,
    val end: Long = System.currentTimeMillis()
)
