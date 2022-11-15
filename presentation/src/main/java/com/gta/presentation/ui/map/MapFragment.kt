package com.gta.presentation.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentMapBinding
import com.gta.presentation.ui.base.BaseFragment
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource

class MapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
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
        mapView = view.findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        activityResultLauncher.launch(permissions)

        val setting = naverMap.uiSettings

        with(setting) {
            isCompassEnabled = true
            isScaleBarEnabled = true
            isLocationButtonEnabled = true
        }

        mapView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    binding.cgFilter.visibility = View.GONE
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    binding.cgFilter.visibility = View.VISIBLE
                }
            }
            mapView.onTouchEvent(event)
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
