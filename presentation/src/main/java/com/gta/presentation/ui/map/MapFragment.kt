package com.gta.presentation.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentMapBinding
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.ui.cardetail.CarDetailViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map), OnMapReadyCallback {
    private val viewModel: CarDetailViewModel by viewModels()

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var backPressedCallback: OnBackPressedCallback
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultMap ->
            val isAllGranted = permissions.all { e -> resultMap[e] == true }

            if (isAllGranted) {
                if (!locationSource.isActivated) {
                    naverMap.locationTrackingMode = LocationTrackingMode.None
                } else {
                    naverMap.locationTrackingMode = LocationTrackingMode.Follow
                }
            }
        }

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onMapReady(naverMap: NaverMap) {
        activityResultLauncher.launch(permissions)
        this.naverMap = naverMap
        setupWithMap()
        setupWithMarker()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupWithMap() {
        naverMap.locationSource = locationSource
        naverMap.uiSettings.apply {
            isCompassEnabled = true
            isScaleBarEnabled = true
            isLocationButtonEnabled = true
        }

        binding.mapView.setOnTouchListener { _, event ->
            binding.cgFilter.visibility = when (event.action) {
                MotionEvent.ACTION_MOVE -> View.GONE
                else -> View.VISIBLE
            }
            binding.mapView.onTouchEvent(event)
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun setupWithMarker() {
        val marker = Marker().apply { // 변수를 없애지 않은 이유는 나중에 Firebase에서 들고와야 하니까,, 일단 냅두겠습니다
            this.icon = MarkerIcons.BLACK
            this.iconTintColor = requireContext().getColor(R.color.primaryColor)
            this.position = LatLng(37.36, 127.1052)
            this.map = naverMap

            this.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                binding.cgFilter.visibility = View.GONE
                true
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                } else {
                    requireActivity().finishAffinity()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onPause() {
        binding.mapView.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStop() {
        binding.mapView.onStop()
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (super.isBindingNotNull()) {
            binding.mapView.onSaveInstanceState(outState)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        binding.mapView.onDestroy()
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
        backPressedCallback.remove()
    }

    override fun onLowMemory() {
        binding.mapView.onLowMemory()
        super.onLowMemory()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}
