package com.gta.presentation.ui.cardetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.SimpleCar
import com.gta.domain.model.UserProfile
import com.gta.domain.usecase.cardetail.GetOwnerCarsUseCase
import com.gta.domain.usecase.cardetail.GetOwnerInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OwnerProfileViewModel @Inject constructor(
    args: SavedStateHandle,
    getOwnerInfoUseCase: GetOwnerInfoUseCase,
    getOwnerCarsUseCase: GetOwnerCarsUseCase
) : ViewModel() {

    val owner: StateFlow<UserProfile>
    val carList: StateFlow<List<SimpleCar>>

    init {
        // TODO : Safe Args 연결
        val ownerId = args.get<String>("OWNER_ID") ?: "debug"

        owner = getOwnerInfoUseCase(ownerId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfile("null", "정보 없음", 0.0F, "")
        )

        carList = getOwnerCarsUseCase(ownerId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }
}
