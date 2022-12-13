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
import com.gta.domain.model.DeleteFailException
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.UCMCResult
import com.gta.domain.model.UpdateFailException
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentCarEditBinding
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.util.DateUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
            .setEnd(MaterialDatePicker.thisMonthInUtcMilliseconds() + DateUtil.DAY_TIME_UNIT * 31)
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

        // TODO 보일러 코드 제거
        binding.ivDayEdit.setOnClickListener {
            datePicker.show(childFragmentManager, null)
        }
        binding.tvDay.setOnClickListener {
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
        binding.tvLocation.setOnClickListener {
            findNavController().navigate(
                CarEditFragmentDirections
                    .actionCarDetailEditFragmentToCarEditMapFragment(
                        viewModel.coordinate ?: viewModel.defaultCoordinate
                    )
            )
        }

        binding.btnDone.setOnClickListener {
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

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.price.collectLatest {
                    if (it.isNotEmpty()) {
                        binding.tvPrice.text = decimalFormat.format(it.toInt())
                    }
                }
            }
        }

        binding.etPrice.setOnFocusChangeListener { _, isFocused ->
            with(binding.tvPrice) {
                visibility = if (isFocused) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateState.collectLatest { result ->
                    when (result) {
                        UpdateState.NORMAL -> {
                            binding.btnDone.visibility = View.VISIBLE
                        }
                        UpdateState.LOAD -> {
                            binding.btnDone.isEnabled = false
                            binding.icLoading.root.visibility = View.VISIBLE
                        }
                        else -> {
                            if (result != UpdateState.SUCCESS) {
                                sendSnackBar(getString(R.string.exception_upload_data))
                            } else {
                                finishUpdateMsg.show()
                            }
                            findNavController().navigateUp()
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorEvent.collectLatest {
                    if (it is UCMCResult.Error) {
                        when (it.e) {
                            DeleteFailException() -> {
                                sendSnackBar(getString(R.string.exception_delete_image_part))
                            }
                            UpdateFailException() -> {
                                sendSnackBar(getString(R.string.exception_upload_image_part))
                            }
                            FirestoreException() -> {
                                sendSnackBar(getString(R.string.exception_load_data))
                            }
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
