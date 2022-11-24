package com.gta.presentation.ui.mypage.mycars

import android.view.View

interface OnItemClickListener<T> {
    fun onClick(value: T)
    fun onLongClick(v: View, value: T)
}
