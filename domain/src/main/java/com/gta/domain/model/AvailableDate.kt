package com.gta.domain.model

data class AvailableDate(
    val start: Long = 0,
    val end: Long = 0
)

fun AvailableDate.toPair(): Pair<Long, Long> {
    return start to end
}

fun List<AvailableDate>.toPairList(): List<Pair<Long, Long>> {
    return this.map { it.toPair() }
}
