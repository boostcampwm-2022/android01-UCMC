package com.gta.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class TransactionUserState : Parcelable {
    LENDER, OWNER
}