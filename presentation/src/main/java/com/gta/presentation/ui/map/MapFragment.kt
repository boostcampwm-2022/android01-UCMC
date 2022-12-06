package com.gta.presentation.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.gta.domain.model.Coordinate
import com.gta.domain.model.LocationInfo
import com.gta.domain.model.SimpleCar
import com.gta.domain.model.UCMCResult
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentMapBinding
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.ui.mypage.mycars.OnItemClickListener
import com.gta.presentation.util.repeatOnStarted
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
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
    private var mapMode = LocationTrackingMode.None
    private val viewModel: MapViewModel by viewModels()
    private lateinit var inputManager: InputMethodManager

    private val markerList = mutableListOf<Marker>()
    private var selectedMarker: Marker? = null

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

    private val bottomSheetCallback = object :
        BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {}
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            naverMap.setContentPadding(
                0,
                0,
                0,
                (binding.bottomSheet.height * (slideOffset + 1)).toInt()
            )
        }
    }

    private val cameraListener = NaverMap.OnCameraChangeListener { _, _ ->
        getNearCars()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        viewModel.startCollect()
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
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
        naverMap.locationTrackingMode = mapMode

        naverMap.extent = LatLngBounds(MAP_MIN_BOUND, MAP_MAX_BOUND)
        naverMap.minZoom = MAP_MIN_ZOOM
        naverMap.maxZoom = MAP_MAX_ZOOM

        naverMap.uiSettings.apply {
            isCompassEnabled = true
            isScaleBarEnabled = true
            isLocationButtonEnabled = true
        }
        getNearCars()

        binding.mapView.setOnTouchListener { _, event ->
            hideKeyboard()
            binding.etSearch.clearFocus()
            if (event.y >= binding.bottomSheet.top && event.y <= binding.bottomSheet.bottom) {
                true
            } else {
                selectedMarker = null
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                binding.mapView.onTouchEvent(event)
            }
        }

        naverMap.addOnCameraChangeListener(cameraListener)
    }

    private fun setupWithMarker() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.carsResponse.collectLatest { result ->
                when (result) {
                    is UCMCResult.Success -> {
                        resetMarkers()
                        result.data.forEach { car ->
                            markerList.add(createMarker(car))
                        }
                    }
                    is UCMCResult.Error -> {
                        sendSnackBar(result.e.message ?: "알 수 없는 오류입니다.")
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
                binding.etSearch.setText(value.name ?: value.address)
                naverMap.moveCamera(CameraUpdate.zoomTo(MAP_FOCUS_ZOOM))
                naverMap.moveCamera(
                    CameraUpdate.scrollTo(LatLng(value.latitude, value.longitude))
                        .animate(CameraAnimation.Easing)
                )
                hideKeyboard()
                binding.etSearch.clearFocus()
                getNearCars()
            }

            override fun onLongClick(v: View, value: LocationInfo) {}
        })
        binding.etSearch.setAdapter(menuAdapter)
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            viewModel.setQuery(text.toString())
        }

        binding.btnDeleteSearch.setOnClickListener {
            binding.etSearch.setText("")
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.searchResponse.collectLatest { result ->
                when (result) {
                    is UCMCResult.Success -> {
                        menuAdapter.replace(result.data)
                    }
                    is UCMCResult.Error -> {
                        sendSnackBar(result.e.message)
                    }
                }
            }
        }
    }

    private fun getNearCars() {
        markerList.forEach {
            setMarkerColor(it, selectedMarker?.position == it.position)
        }
        naverMap.let { naverMap ->
            var minLat = 91.0
            var maxLat = -91.0
            var minLng = 181.0
            var maxLng = -181.0

            listOf(
                PointF(binding.mapView.left.toFloat(), binding.mapView.top.toFloat()),
                PointF(binding.mapView.left.toFloat(), binding.mapView.bottom.toFloat()),
                PointF(binding.mapView.right.toFloat(), binding.mapView.top.toFloat()),
                PointF(binding.mapView.right.toFloat(), binding.mapView.bottom.toFloat())
            ).forEach { location ->
                val tempLocation = naverMap.projection.fromScreenLocation(location)

                if (tempLocation.latitude < minLat) {
                    minLat = tempLocation.latitude
                }
                if (tempLocation.latitude > maxLat) {
                    maxLat = tempLocation.latitude
                }

                if (tempLocation.longitude < minLng) {
                    minLng = tempLocation.longitude
                }
                if (tempLocation.longitude > maxLng) {
                    maxLng = tempLocation.longitude
                }
            }
            viewModel.setPosition(Coordinate(minLat, minLng), Coordinate(maxLat, maxLng))
        }
    }

    private fun createMarker(car: SimpleCar): Marker {
        val marker = Marker()
        marker.apply {
            position = LatLng(car.coordinate.latitude, car.coordinate.longitude)
            icon = MarkerIcons.BLACK
            setMarkerColor(this, selectedMarker?.position == position)
            map = naverMap

            setOnClickListener {
                setMarkerColor(selectedMarker, false)
                selectedMarker = this
                setMarkerColor(this, true)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                naverMap.moveCamera(CameraUpdate.zoomTo(MAP_FOCUS_ZOOM))
                naverMap.moveCamera(
                    CameraUpdate.scrollTo(position)
                        .animate(CameraAnimation.Easing)
                )
                viewModel.setSelected(car)
                true
            }
        }

        return marker
    }

    private fun resetMarkers() {
        markerList.forEach {
            it.map = null
        }
        markerList.clear()
    }

    private fun setMarkerColor(marker: Marker?, selected: Boolean) {
        marker?.iconTintColor =
            requireContext().getColor(if (selected) R.color.primaryDarkColor else R.color.primaryColor)
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
        mapMode = naverMap.locationTrackingMode
        binding.mapView.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStop() {
        viewModel.stopCollect()
        naverMap.removeOnCameraChangeListener(cameraListener)
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
        bottomSheetBehavior.removeBottomSheetCallback(bottomSheetCallback)
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
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000

        private const val MAP_MIN_ZOOM = 5.0
        private const val MAP_FOCUS_ZOOM = 15.0
        private const val MAP_MAX_ZOOM = 18.0

        private val MAP_MIN_BOUND = LatLng(31.43, 122.37)
        private val MAP_MAX_BOUND = LatLng(44.35, 132.0)
    }
}
