package com.example.market.ui.map

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
import androidx.navigation.fragment.findNavController
import com.example.market.data.pojo.UserAddress
import com.example.market.databinding.FragmentMapBinding
import com.example.market.utils.Constants.ADDRESS_KEY
import com.example.market.utils.Constants.CITY_KEY
import com.example.market.utils.Constants.GOVERN_KEY
import com.example.market.utils.Constants.MAP
import com.example.market.utils.Constants.POSTAL_KEY
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
    lateinit var binding: FragmentMapBinding
    lateinit var location: Location
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var geocoder: Geocoder
    var latitude: Double? = null
    var longitude: Double? = null
    private var isLocationViaGPS = true
    lateinit var userAddress: UserAddress

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
        geocoder = Geocoder(requireContext(), Locale("en"))
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        binding.btnToggleLocationMethod.setOnClickListener {
            isLocationViaGPS = true
            getLastLocation()
        }
        binding.okBtn.setOnClickListener {
            if (latitude != null && longitude != null) {
                val addressList =
                    geocoder.getFromLocation(latitude!!, longitude!!, 1)

                if (addressList != null && addressList.isNotEmpty()) {
                    Log.i("MAP", "$addressList")
                    val address = addressList[0]
                    if (address.countryCode != null && address.countryCode == "EG") {
                        val newLocality = addressList[0].locality
                        val subAdmin = addressList[0].subAdminArea
                        val subLocality = addressList[0].subLocality

                        val cityName = newLocality ?: subAdmin ?: subLocality
                        if (cityName != null) {
                            val government = address.adminArea?:""
                            val city = address.subAdminArea?:""
                            val postalCode = address.postalCode?:""
                            val feature = address.featureName
                            val addressDetails = "$feature-$city-$government-GE"
                            userAddress = UserAddress(
                                addressDetails,
                                city,
                                government,
                                null,
                                postalCode,
                                null,
                                null,
                                address.countryCode
                            )
                            parentFragmentManager.setFragmentResult(MAP, Bundle().apply {
                                putString(ADDRESS_KEY, userAddress.address1)
                                putString(POSTAL_KEY, userAddress.zip)
                                putString(GOVERN_KEY, userAddress.province)
                                putString(CITY_KEY, userAddress.city)
                                findNavController().navigateUp()
                            })
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
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "must choose from egypt only",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } else {
                Toast.makeText(requireContext(), "choose location first", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener(this)
//        latitude = 30.04

//        longitude = 31.23
//        val cairo = LatLng(latitude!!, longitude!!)
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(cairo))
    }

    override fun onMapClick(latLng: LatLng) {
        isLocationViaGPS = false
        mMap.clear()
        latitude = latLng.latitude
        longitude = latLng.longitude
        mMap.addMarker(MarkerOptions().position(latLng).title("New Marker"))
    }


    private var mLocationCallBack: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            val mLastLocation: Location? = p0.lastLocation
            when (isLocationViaGPS) {
                true -> {
                    if (mLastLocation != null) {
                        location = mLastLocation
                        mMap.clear()
                        latitude = location.latitude
                        longitude = location.longitude
                        val latLng = LatLng(location.latitude, location.longitude)
                        mMap.addMarker(MarkerOptions().position(latLng).title("new marker"))
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                        mFusedLocationClient.removeLocationUpdates(this)
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