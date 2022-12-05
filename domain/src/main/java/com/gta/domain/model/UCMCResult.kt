package com.gta.domain.model

sealed class UCMCResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : UCMCResult<T>()
    data class Error(val e: Exception) : UCMCResult<Nothing>()
}
