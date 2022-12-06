package com.gta.presentation.ui.cardetail.edit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.gta.domain.model.Coordinate
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentCarEditBinding
import com.gta.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.DecimalFormat

@AndroidEntryPoint
class CarEditFragment : BaseFragment<FragmentCarEditBinding>(
    R.layout.fragment_car_edit
) {

    private val viewModel: CarEditViewModel by viewModels()
    private val imagesAdapter by lazy { CarEditImagesAdapter() }

    val decimalFormat = DecimalFormat("#,###")
    private val MAX_IMAGE = 10

    private val maxImagesMsg: Snackbar by lazy {
        Snackbar.make(
            binding.btnDone,
            resources.getString(R.string.car_edit_max_image),
            Snackbar.LENGTH_SHORT
        ).apply {
            anchorView = binding.btnDone
        }
    }

    private val finishUpdateMsg: Snackbar by lazy {
        Snackbar.make(
            binding.btnDone,
            resources.getString(R.string.car_edit_finish_update),
            Snackbar.LENGTH_SHORT
        ).apply {
            anchorView = binding.btnDone
        }
    }

    private val constraints: CalendarConstraints by lazy {
        CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
            .setEnd(MaterialDatePicker.thisMonthInUtcMilliseconds())
            .build()
    }
    private val datePicker by lazy {
        MaterialDatePicker.Builder
            .dateRangePicker()
            .setTheme(R.style.Theme_UCMC_DatePicker)
            .setCalendarConstraints(constraints)
            .build()
    }

    private val imageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.updateImage(result.data?.data.toString())
            binding.rvImages.scrollToPosition(0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding.vm = viewModel
        binding.rvImages.adapter = imagesAdapter.apply {
            setItemClickListener(object : CarEditImagesAdapter.OnItemClickListener {
                override fun onClick(position: Int) {
                    viewModel.deleteImage(position)
                }
            })
        }

        datePicker.addOnPositiveButtonClickListener {
            viewModel.setAvailableDate(it.first, it.second)
        }

        // 위치 수정 값
        setChangeLocation()

        return binding.root
    }

    private fun setChangeLocation() {
        val text = findNavController()
            .currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>("LOCATION")?.value
        val latitude = findNavController()
            .currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>("LATITUDE")?.value
        val longitude = findNavController()
            .currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>("LONGITUDE")?.value

        if (text != null && latitude != null && longitude != null) {
            viewModel.setLocationData(
                text,
                latitude.toDouble(),
                longitude.toDouble()
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddImage.setOnClickListener {
            if (viewModel.images.value.size < MAX_IMAGE) {
                addImageAtGallery()
            } else {
                maxImagesMsg.show()
            }
        }

        binding.ivDayEdit.setOnClickListener {
            datePicker.show(childFragmentManager, null)
        }

        binding.ivLocationEdit.setOnClickListener {
            findNavController().navigate(
                CarEditFragmentDirections
                    .actionCarDetailEditFragmentToCarEditMapFragment(
                        viewModel.coordinate ?: viewModel.defaultCoordinate
                    )
            )
        }

        binding.btnDone.setOnClickListener {
            // TODO 확인 작업
            viewModel.updateData()
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.images.collectLatest {
                    imagesAdapter.submitList(it)

                    binding.btnAddImage.text =
                        String.format(
                            resources.getString(R.string.car_edit_images_count),
                            it.size
                        )
                }
            }
        }

        binding.etPrice.setOnFocusChangeListener { _, boolean ->
            with(binding.tvPrice) {
                if (boolean) {
                    visibility = View.GONE
                } else {
                    text = decimalFormat.format(viewModel.price.value.toInt())
                    visibility = View.VISIBLE
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateState.collectLatest { result ->
                    when (result) {
                        UpdateState.NORMAL -> {
                            binding.icLoading.root.visibility = View.GONE
                        }
                        UpdateState.LOAD -> {
                            binding.icLoading.root.visibility = View.VISIBLE
                        }
                        UpdateState.SUCCESS -> {
                            // TODO 완료 sanckbar가 too much 인지 생각
                            finishUpdateMsg.show()
                            findNavController().navigateUp()
                        }
                        else -> {
                            Timber.d("차 상세 데이터 update 실패")
                        }
                    }
                }
            }
        }
    }

    private fun addImageAtGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
        }
        imageResult.launch(intent)
    }
}
