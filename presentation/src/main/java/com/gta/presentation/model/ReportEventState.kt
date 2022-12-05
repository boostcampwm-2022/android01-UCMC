package com.gta.presentation.model

sealed class ReportEventState {
    object Success : ReportEventState()
    data class Cooldown(val cooldown: Long) : ReportEventState()
    object Error : ReportEventState()
}
