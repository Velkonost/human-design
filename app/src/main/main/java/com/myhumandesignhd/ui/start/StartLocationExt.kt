package com.myhumandesignhd.ui.start

//fun StartFragment.checkLocationPermission() {
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
//
//fun StartFragment.setupLocationListener() {
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
//    mLocationManager.requestLocationUpdates(
//        LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
//        LOCATION_REFRESH_DISTANCE, mLocationListener
//    )
//}
//
//fun StartFragment.requestLocationPermission() {
//    ActivityCompat.requestPermissions(
//        requireActivity(),
//        arrayOf(
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        ),
//        App.LOCATION_REQUEST_CODE
//    )
//}

