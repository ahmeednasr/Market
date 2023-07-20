package com.example.market

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import com.example.market.databinding.FragmentMapBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*


class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {
    lateinit var mMap: GoogleMap
    lateinit var mapView: MapView
    lateinit var binding: FragmentMapBinding
    lateinit var location: Location
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var geocoder: Geocoder
    var latitude: Double? = null
    var longitude: Double? = null
    private var isLocationViaGPS = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        geocoder = Geocoder(requireContext())
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        // Set the initial location method (GPS by default)
        binding.btnToggleLocationMethod.setOnClickListener {
            isLocationViaGPS = true
            getLastLocation()
        }

    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
//        getLastLocation()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Set an OnMapClickListener on the map
        mMap.setOnMapClickListener(this)
        // Add a marker in Sydney and move the camera
        latitude = 30.04
        longitude = 31.23
        val cairo = LatLng(latitude!!, longitude!!)
//        val cairo = LatLng(location.latitude, location.longitude)
        mMap.addMarker(MarkerOptions().position(cairo).title("cairo"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cairo))
    }

    override fun onMapClick(latLng: LatLng) {
        isLocationViaGPS = false
        mMap.clear()
        latitude = latLng.latitude
        longitude = latLng.longitude
        val addressList =
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        Log.i("err", "address name: $addressList")
        if (addressList != null && addressList.isNotEmpty()) {

            val newLocality = addressList[0].locality
            val subAdmin = addressList[0].subAdminArea
            val subLocality = addressList[0].subLocality

            val cityName = newLocality ?: subAdmin ?: subLocality
            if (cityName != null) {
                Toast.makeText(
                    requireContext(),
                    cityName,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "name not found",
                    Toast.LENGTH_SHORT
                ).show()
            }

            Log.i("err", "City in name: $cityName")
        }

        mMap.addMarker(MarkerOptions().position(latLng).title("New Marker"))
    }


    private var mLocationCallBack: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            val mLastLocation: Location? = p0.lastLocation
            when (isLocationViaGPS) {
                true -> {
                    if (mLastLocation != null) {
                        val addressList =
                            geocoder.getFromLocation(
                                mLastLocation.latitude,
                                mLastLocation.longitude,
                                1
                            )
                        if (addressList != null && addressList.isNotEmpty()) {
                            var newLocality = addressList[0].locality
                            location = mLastLocation
                            mMap.clear()
                            val latLng = LatLng(location.latitude, location.longitude)
                            mMap.addMarker(MarkerOptions().position(latLng).title("$newLocality"))
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                            mFusedLocationClient.removeLocationUpdates(this)
                        }
                    }
                }
                false -> {
                    mFusedLocationClient.removeLocationUpdates(this)
                }
            }
        }
    }


    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                requestNewLocationData()
            } else {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        val result = ActivityCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return result
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            5005
        )
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        //base
        val mLocationRequest = LocationRequest()
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest.setInterval(1000)
        //ref of locations
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallBack,
            Looper.myLooper()
        )
    }
}