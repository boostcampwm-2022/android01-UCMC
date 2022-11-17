package com.gta.presentation.ui.pinkslip.registration

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.gta.domain.model.PinkSlip
import com.gta.domain.usecase.pinkslip.GetPinkSlipUseCase
import com.gta.domain.usecase.pinkslip.SetPinkSlipUseCase
import com.gta.presentation.util.ImageUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
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

    private val _registerEvent = MutableSharedFlow<Boolean>()
    val registerEvent: SharedFlow<Boolean> get() = _registerEvent

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
            _pinkSlip.emit(getPinkSlipUseCase(buffer).first())
        }
    }

    fun registerPinkSlip() {
        val pinkSlip = pinkSlip.value ?: return
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _registerEvent.emit(setPinkSlipUseCase(uid, pinkSlip).first())
        }
    }
}
