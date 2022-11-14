package com.gta.presentation.ui.map

import android.os.Bundle
import android.view.View
import androidx.annotation.UiThread
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentMapBinding
import com.gta.presentation.ui.base.BaseFragment
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback

class MapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map), OnMapReadyCallback {
    private lateinit var mapView: MapView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync(this)
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

    @UiThread
    override fun onMapReady(map: NaverMap) {
        val setting = map.uiSettings

        with(setting) {
            isCompassEnabled = true
            isScaleBarEnabled = true
            isLocationButtonEnabled = true
        }

    }
}
