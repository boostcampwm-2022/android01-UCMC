package com.gta.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class TransactionState(val state: String) : Parcelable {
    TRADING("거래중"), COMPLETED("거래완료")
}