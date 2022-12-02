package com.gta.presentation.ui.review

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.ReviewDTO
import com.gta.domain.model.UserReview
import com.gta.domain.usecase.review.AddReviewUseCase
import com.gta.domain.usecase.review.GetReviewDTOUseCase
import com.gta.presentation.util.FirebaseUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    args: SavedStateHandle,
    private val addReviewUseCase: AddReviewUseCase,
    private val getReviewDTOUseCase: GetReviewDTOUseCase
) : ViewModel() {

    val comment = MutableStateFlow("")
    val rating = MutableStateFlow(5.0f)

    private val reservationId = args.get<String>("reservationId") ?: ""

    private val _reviewDTO = MutableStateFlow(ReviewDTO())
    val reviewDTO: StateFlow<ReviewDTO> get() = _reviewDTO

    private val _addReviewEvent = MutableSharedFlow<Boolean>()
    val addReviewEvent: SharedFlow<Boolean> get() = _addReviewEvent

    init {
        // 예약 id로 이미 리뷰를 했는지 검사가 필요
        // safe arg로 예약 id를 받아와야 합니다.
        getReviewDTO(reservationId)
    }

    private fun getReviewDTO(reservationId: String) {
        viewModelScope.launch {
            _reviewDTO.emit(getReviewDTOUseCase(FirebaseUtil.uid, reservationId).first())
        }
    }

    fun addReview() {
        viewModelScope.launch {
            _addReviewEvent.emit(
                addReviewUseCase(
                    opponentId = reviewDTO.value.opponent.id,
                    reservationId = reservationId,
                    review = getUserReview()
                ).first()
            )
        }
    }

    private fun getUserReview() =
        UserReview(
            from = FirebaseUtil.uid,
            comment = comment.value,
            rating = rating.value
        )
}
