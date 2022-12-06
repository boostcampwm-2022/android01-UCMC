package com.gta.presentation.ui.pinkslip.registration

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.gta.domain.model.PinkSlip
import com.gta.domain.model.UCMCResult
import com.gta.domain.usecase.pinkslip.GetPinkSlipUseCase
import com.gta.domain.usecase.pinkslip.SetPinkSlipUseCase
import com.gta.presentation.util.ImageUtil
import com.gta.presentation.util.MutableEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PinkSlipRegistrationViewModel @Inject constructor(
    private val imageUtil: ImageUtil,
    private val auth: FirebaseAuth,
    private val getPinkSlipUseCase: GetPinkSlipUseCase,
    private val setPinkSlipUseCase: SetPinkSlipUseCase,
    args: SavedStateHandle
) : ViewModel() {

    private val _pinkSlip = MutableStateFlow<PinkSlip?>(null)
    val pinkSlip: StateFlow<PinkSlip?> get() = _pinkSlip

    private val _registerEvent = MutableEventFlow<UCMCResult<Unit>>()
    val registerEvent get() = _registerEvent

    private val _pinkSlipPicture = MutableStateFlow("")
    val pinkSlipPicture: StateFlow<String> get() = _pinkSlipPicture

    init {
        args.get<String>("uri")?.let { uri ->
            viewModelScope.launch {
                _pinkSlipPicture.emit(uri)
            }
            getPinkSlip(uri)
        }
    }

    private fun getPinkSlip(uri: String) {
        val buffer = imageUtil.getByteBuffer(uri) ?: return
        viewModelScope.launch {
            _pinkSlip.emit(getPinkSlipUseCase(buffer))
        }
    }

    fun registerPinkSlip() {
        val pinkSlip = pinkSlip.value ?: return
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _registerEvent.emit(setPinkSlipUseCase(uid, pinkSlip))
        }
    }
}
