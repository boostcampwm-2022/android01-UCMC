package com.gta.presentation.ui.reservation

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.gta.domain.model.AvailableDate
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentReservationBinding
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.util.DateValidator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@AndroidEntryPoint
class ReservationFragment :
    BaseFragment<FragmentReservationBinding>(R.layout.fragment_reservation) {
    private val args: ReservationFragmentArgs by navArgs()
    private val carInfo by lazy { args.carInfo }

    @Inject
    lateinit var viewModelFactory: ReservationViewModel.AssistedFactory

    private val viewModel: ReservationViewModel by viewModels {
        ReservationViewModel.provideFactory(
            assistedFactory = viewModelFactory,
            carInfo = carInfo
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        setupDatePicker()

        return binding.run {
            vm = viewModel
            carInfo = this@ReservationFragment.carInfo
            root
        }
    }

    private fun setupDatePicker() {
        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidator(carInfo.reservationDate, null))
            .setStart(carInfo.reservationDate.first)
            .setEnd(carInfo.reservationDate.second)
            .build()

        val datePicker = MaterialDatePicker.Builder
            .dateRangePicker()
            .setTheme(R.style.Theme_UCMC_DatePicker)
            .setCalendarConstraints(constraints)
            .build()

        datePicker.addOnPositiveButtonClickListener {
            viewModel.setReservationDate(AvailableDate(it.first, it.second))
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
    val reservationDate: Pair<Long, Long>
) : Parcelable
