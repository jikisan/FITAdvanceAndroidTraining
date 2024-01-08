package com.example.day3geocoderactivity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.day3geocoderactivity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), LocationListener {

    lateinit var binding: ActivityMainBinding
    lateinit var geocoder: Geocoder
    lateinit var locationManager: LocationManager

    var latitude : Double = 0.0
    var longitude : Double = 0.0

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        geocoder = Geocoder(this)
        locationManager = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),0)

            return
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0.toLong(),0.toFloat(),this)

        binding.btnSearchCoor.setOnClickListener {

            findCoordinates(binding.txtCoorAddress.text.toString())

        }

        binding.btnMapsCoor.setOnClickListener {
            val uri = Uri.parse("geo:$latitude,$longitude?q=atm")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        }

        binding.btnFindAddress.setOnClickListener {
            val lat = binding.txtAddressLatitude.text.toString().toDouble()
            val long = binding.txtAddressLongtitude.text.toString().toDouble()
            findAddress(lat, long)
        }
    }

    fun findCoordinates(address: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            geocoder.getFromLocationName(address, 1, object : GeocodeListener {
                override fun onGeocode(addressess: MutableList<Address>) {
                Log.d("Version", "Tiramisu")
                    if (addressess.size > 0) {
                        val result = addressess[0]
//                        binding.lblCoorResult.text = "Lat: ${result.latitude}  Long: ${result.longitude}"
                        binding.lblCoorResult.setText("Lat: ${result.latitude}  Long: ${result.longitude}")
                        latitude = result.latitude
                        longitude = result.longitude
                        binding.txtAddressLatitude.setText("${result.latitude}")
                        binding.txtAddressLongtitude.setText("${result.longitude}")
                    }
                    else {
                        binding.lblCoorResult.text = "Location not found."
                    }
                }

            })
        }
        else {
            val addressess = geocoder.getFromLocationName(address, 1)
            if(addressess?.size!! > 0) {
                Log.d("Version", "Not Tiramisu")
                val result = addressess[0]
                binding.lblCoorResult.text = "Lat: ${result.latitude} Long: ${result.longitude}"
                latitude = result.latitude
                longitude = result.longitude
                binding.txtAddressLatitude.setText("${result.latitude}")
                binding.txtAddressLongtitude.setText("${result.longitude}")
            }
            else {
                binding.lblCoorResult.text = "Location not found."
            }
        }
    }

    fun findAddress(latitude: Double, longitude: Double) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(latitude, longitude, 1, object : GeocodeListener {
                override fun onGeocode(addressess: MutableList<Address>) {
                    if (addressess.size > 0) {
                        val result = addressess[0]
                        binding.lblAddResult.text = "${result.getAddressLine(0)} ${result.adminArea} ${result.locality}" +
                                "${result.countryName}, ${result.countryCode}"
                    }
                    else {
                        binding.lblAddResult.text = "Location not found."
                    }
                }


            })
        }
        else {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses?.size!! > 0) {
                val result = addresses[0]
                binding.lblAddResult.text = "${result.getAddressLine(0)} ${result.adminArea} ${result.locality}" +
                        "${result.countryName}, ${result.countryCode}"
            }
            else {
                binding.lblAddResult.text = "Address not found"
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        binding.lblLocAddress.text = "Latitude: ${location.latitude}"
        binding.lblLocCoordinates.text = "Longited: ${location.longitude}"
        findAddress(location.latitude, location.longitude)
    }

}