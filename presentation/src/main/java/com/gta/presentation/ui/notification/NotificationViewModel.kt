package com.gta.presentation.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gta.domain.model.NotificationInfo
import com.gta.domain.usecase.notification.GetNotificationsInfoUseCase
import com.gta.presentation.util.FirebaseUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    getNotificationInfo: GetNotificationsInfoUseCase
) : ViewModel() {

    private val _notifyList = MutableStateFlow<PagingData<NotificationInfo>>(PagingData.empty())
    val notificationList: Flow<PagingData<NotificationInfo>>
        get() = _notifyList

    init {
        viewModelScope.launch {
            _notifyList.value = getNotificationInfo(FirebaseUtil.uid).cachedIn(viewModelScope).first()
        }
    }
}
