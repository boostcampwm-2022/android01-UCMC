package com.gta.presentation.ui.cardetail.edit

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gta.domain.model.LocationInfo
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentCarEditMapBinding
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.ui.map.AutoCompleteAdapter
import com.gta.presentation.ui.map.MapViewModel
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
import timber.log.Timber

@AndroidEntryPoint
class CarEditMapFragment :
    BaseFragment<FragmentCarEditMapBinding>(R.layout.fragment_car_edit_map),
    OnMapReadyCallback {
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var menuAdapter: AutoCompleteAdapter
    private var mapMode = LocationTrackingMode.None
    private val viewModel: MapViewModel by viewModels()
    private lateinit var inputManager: InputMethodManager

    private val marker: Marker = Marker()

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

        viewModel.startCollect()
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        // location 지정을 위한 작업 시작
        marker.apply {
            icon = MarkerIcons.BLACK
            iconTintColor = requireContext().getColor(R.color.primaryColor)
        }

        binding.btnDone.setOnClickListener {
            marker.apply {
                position = naverMap.cameraPosition.target
                map = naverMap
            }

            setVisibleAtSelectLocation(true)
        }

        binding.btnLocationCancle.setOnClickListener {
            setVisibleAtSelectLocation(false)
            marker.map = null
        }

        binding.btnLocationDone.setOnClickListener {
            // TODO 완료 안내 메시지 추가 고민
            
            saveLocationData("LOCATION", binding.etLocationInput.text.toString())
            saveLocationData("LATITUDE", marker.position.latitude.toString())
            saveLocationData("LONGITUDE", marker.position.longitude.toString())

            findNavController().navigateUp()
        }
    }

    private fun setVisibleAtSelectLocation(state: Boolean) {
        binding.layoutDone.visibility = if (state) View.VISIBLE else View.GONE
        binding.ivMyCar.visibility = if (!state) View.VISIBLE else View.GONE
    }

    private fun saveLocationData(key: String, value: String) {
        findNavController()
            .previousBackStackEntry?.savedStateHandle?.set(
                key,
                value
            )
    }

    override fun onMapReady(naverMap: NaverMap) {
        activityResultLauncher.launch(permissions)
        this.naverMap = naverMap
        setupWithMap()
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

        binding.mapView.setOnTouchListener { _, event ->
            hideKeyboard()
            binding.etSearch.clearFocus()
            binding.mapView.onTouchEvent(event)
        }
    }

    private fun setupWithSearch() {
        menuAdapter = AutoCompleteAdapter(requireContext(), emptyList())
        menuAdapter.setOnItemClickListener(object : OnItemClickListener<LocationInfo> {
            override fun onClick(value: LocationInfo) {
                binding.etSearch.setText(value.name ?: value.address)
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
            viewModel.setQuery(text.toString())
        }

        binding.btnDeleteSearch.setOnClickListener {
            binding.etSearch.setText("")
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.searchResponse.collectLatest { list ->
                menuAdapter.replace(list)
            }
        }
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
        private const val MAP_MAX_ZOOM = 18.0

        private val MAP_MIN_BOUND = LatLng(31.43, 122.37)
        private val MAP_MAX_BOUND = LatLng(44.35, 132.0)
    }
}
