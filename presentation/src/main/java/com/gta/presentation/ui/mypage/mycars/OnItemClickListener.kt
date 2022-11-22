package com.gta.presentation.ui.mypage.mycars

import android.view.View

interface OnItemClickListener {
    fun onClick(carId: String)
    fun onLongClick(v: View, carId: String)
}
