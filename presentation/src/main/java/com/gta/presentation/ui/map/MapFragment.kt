package com.gta.presentation.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.gta.domain.model.LocationInfo
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentMapBinding
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.ui.mypage.mycars.OnItemClickListener
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map), OnMapReadyCallback {
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var backPressedCallback: OnBackPressedCallback
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private lateinit var menuAdapter: AutoCompleteAdapter

    private val viewModel: MapViewModel by viewModels()
    private lateinit var inputManager: InputMethodManager

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
        setupWithBottomSheet()
        setupWithSearch()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupWithMap() {
        naverMap.locationSource = locationSource
        naverMap.uiSettings.apply {
            isCompassEnabled = true
            isScaleBarEnabled = true
            isLocationButtonEnabled = true
        }

        binding.mapView.setOnTouchListener { v, event ->
            if (event.y >= binding.bottomSheet.top && event.y <= binding.bottomSheet.bottom) {
                true
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                binding.mapView.onTouchEvent(event)
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun setupWithMarker() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cars.collectLatest {
                    it.forEach { car ->
                        Marker().apply {
                            icon = MarkerIcons.BLACK
                            iconTintColor = requireContext().getColor(R.color.primaryColor)
                            position = LatLng(car.coordinate.x, car.coordinate.y)
                            map = naverMap

                            setOnClickListener {
                                viewModel.emitSelected(car)
                                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                                true
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupWithBottomSheet() {
        binding.bottomSheet.setOnTouchListener { _, _ ->
            val navAction =
                MapFragmentDirections.actionMapFragmentToCarDetailFragment(viewModel.selectCar.value.id)
            findNavController().navigate(navAction)
            false
        }
    }

    private fun setupWithSearch() {
        menuAdapter = AutoCompleteAdapter(requireContext(), emptyList())
        menuAdapter.setOnItemClickListener(object : OnItemClickListener<LocationInfo> {
            override fun onClick(value: LocationInfo) {
                binding.etSearch.setText(value.address)
                naverMap.moveCamera(
                    CameraUpdate.scrollTo(LatLng(value.latitude, value.longitude))
                        .animate(CameraAnimation.Easing)
                )
                hideKeyboard()
                binding.etSearch.clearFocus()
            }

            override fun onLongClick(v: View, value: LocationInfo) {}
        })
        binding.etSearch.setAdapter(menuAdapter)
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            viewModel.queryFlow.tryEmit(text.toString())
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchResult.collectLatest { list ->
                    menuAdapter.replace(list)
                }
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

    private fun hideKeyboard() {
        requireActivity().also { activity ->
            if (activity.currentFocus != null) {
                inputManager =
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                activity.currentFocus?.let { view ->
                    inputManager.hideSoftInputFromWindow(
                        view.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
                }
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}
