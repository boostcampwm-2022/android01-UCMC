package com.gta.presentation.ui.cardetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.gta.domain.usecase.cardetail.GetOwnerCarsUseCase
import com.gta.domain.usecase.cardetail.GetOwnerInfoUseCase
import javax.inject.Inject

class OwnerProfileViewModel @Inject constructor(
    args: SavedStateHandle,
    getOwnerInfoUseCase: GetOwnerInfoUseCase,
    getOwnerCarsUseCase: GetOwnerCarsUseCase
) : ViewModel() {
    init {
        /*
        // TODO : Safe Args 연결
        ownerId = args.get<String>("OWNER_ID") ?: "debug"
         */
    }
}
