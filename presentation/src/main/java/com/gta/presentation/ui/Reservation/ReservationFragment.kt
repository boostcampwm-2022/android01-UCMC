package com.gta.presentation.ui.reservation

import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentReservationBinding
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.util.DateValidator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import timber.log.Timber

class ReservationFragment :
    BaseFragment<FragmentReservationBinding>(R.layout.fragment_reservation) {
    private val args: ReservationFragmentArgs by navArgs()
    private val carInfo by lazy { args.carInfo }

    override fun onStart() {
        super.onStart()

        // 임시 불가능한 날짜들
        val invalidateDates = listOf(1669248000000 to 1669420800000)

        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidator(carInfo.reservationRange, invalidateDates))
            .setStart(carInfo.reservationRange.first)
            .setEnd(carInfo.reservationRange.second)
            .build()

        val datePicker = MaterialDatePicker.Builder
            .dateRangePicker()
            .setTheme(R.style.Theme_UCMC_DatePicker)
            .setCalendarConstraints(constraints)
            .build()

        datePicker.addOnPositiveButtonClickListener {
            Timber.d(it.toString())
        }

        binding.ivReservationNext.setOnClickListener {
            datePicker.show(childFragmentManager, null)
        }
    }
}

// 임시 자동차 객체
@Parcelize
data class TmpCarInfo(
    val id: String,
    val title: String,
    val location: String,
    val carType: String,
    val price: Int,
    val comment: String,
    val images: List<String>,
    val reservationRange: Pair<Long, Long>
) : Parcelable