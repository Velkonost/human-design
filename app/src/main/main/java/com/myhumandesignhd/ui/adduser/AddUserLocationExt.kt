package com.myhumandesignhd.ui.adduser

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

//fun AddUserFragment.checkLocationPermission() {
//    if (ActivityCompat.checkSelfPermission(
//            requireContext(),
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ) != PackageManager.PERMISSION_GRANTED
//    ) {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(
//                requireActivity(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            )
//        ) {
//            AlertDialog.Builder(requireContext())
//                .setTitle("Location Permission Needed")
//                .setMessage("This app needs the Location permission, please accept to use location functionality")
//                .setPositiveButton(
//                    "OK"
//                ) { _, _ ->
//                    requestLocationPermission()
//                }
//                .create()
//                .show()
//        } else {
//            requestLocationPermission()
//        }
//    }
//}

//fun AddUserFragment.setupLocationListener() {
//    if (ActivityCompat.checkSelfPermission(
//            requireContext(),
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//            requireContext(),
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        ) != PackageManager.PERMISSION_GRANTED
//    ) {
//        checkLocationPermission()
//        return
//    }
//
//    val lm = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
//    if(LocationManagerCompat.isLocationEnabled(lm)) {
//        // you can do this your own way, eg. from a viewModel
//        // but here is where you wanna start the coroutine.
//        // Choose your priority based on the permission you required
//        val priority = PRIORITY_HIGH_ACCURACY
//        lifecycleScope.launch {
//            val location = LocationServices
//                .getFusedLocationProviderClient(requireContext())
//                .awaitCurrentLocation(priority)
//            // do whatever with this location, notice that it's nullable
//
////            if (::geocoder.isInitialized) {
//                kotlin.runCatching {
//                    val currentLocationVariants =
//                        geocoder.getFromLocation(location!!.latitude, location.longitude, 10)
//
//                    if (
//                        currentLocationVariants.isNotEmpty()
//                        && currentLocationVariants.any { variant ->
//                            !variant.locality.isNullOrEmpty() && !variant.countryName.isNullOrEmpty()
//                        }
//                    ) {
//
////                        isCurrentLocationVariantSet = true
////
//                        selectedLat = currentLocationVariants[0].latitude.toString()
//                        selectedLon = currentLocationVariants[0].longitude.toString()
//                        binding.viewModel!!.reverseGeocoding(currentLocationVariants[0].latitude.toFloat(), currentLocationVariants[0].longitude.toFloat())
////                        binding.placeET.setText("${currentLocationVariants[0].locality}, ${currentLocationVariants[0].countryName}")
//                    }
//                }
////            }
//        }
//    } else {
//        // prompt user to enable location or launch location settings check
//    }
//
////    mLocationManager.requestLocationUpdates(
////        LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
////        LOCATION_REFRESH_DISTANCE, mLocationListener
////    )
//}

//fun AddUserFragment.requestLocationPermission() {
//    ActivityCompat.requestPermissions(
//        requireActivity(),
//        arrayOf(
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        ),
//        App.LOCATION_REQUEST_CODE
//    )
//}

// To use PRIORITY_HIGH_ACCURACY, you must have ACCESS_FINE_LOCATION permission.
// Any other priority will require just ACCESS_COARSE_LOCATION,
// but will not guarantee a location update
@SuppressLint("MissingPermission")
suspend fun FusedLocationProviderClient.awaitCurrentLocation(priority: Int): Location? {
    return suspendCancellableCoroutine {
        // to use for request cancellation upon coroutine cancellation
        val cts = CancellationTokenSource()
        getCurrentLocation(priority, cts.token)
            .addOnSuccessListener {location ->
                // remember location is nullable, this happens sometimes
                // when the request expires before an update is acquired
                it.resume(location) {

                }
            }.addOnFailureListener {e ->
                it.resumeWithException(e)
            }

        it.invokeOnCancellation {
            cts.cancel()
        }
    }
}
