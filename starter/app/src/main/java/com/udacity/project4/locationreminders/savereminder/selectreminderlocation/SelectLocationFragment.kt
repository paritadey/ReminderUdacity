package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import kotlinx.android.synthetic.main.activity_reminders.*
import org.koin.android.ext.android.inject

class SelectLocationFragment : BaseFragment() {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocation: Location = Location("")
    private lateinit var latitude: String
    private lateinit var longitude: String
    private var mapMode: Int = -1
    private val REQUEST_CODE = 200

    companion object {
        private const val DEFAULT_ZOOM = 15f
        var isMyLocationSet = false
    }

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback {
        mMap = it
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return@OnMapReadyCallback
        }
        else{
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)
        //add the map setup implementation
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(callback)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //zoom to the user location after taking his permission
        //add style to the map
        //put a marker to location that the user selected
        //call this function after the user confirms on the selected location
        getRuntimePermissions()
        binding.proceed.setOnClickListener {
            if(getRuntimePermissions())
                onLocationSelected()
            else{
                Snackbar.make(
                    binding.root,
                    "Please allow all the permissions",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun getRuntimePermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), REQUEST_CODE
            )
            return false
        } else {
            fetchingUserLocation()
            return true
        }
    }


    private fun onLocationSelected() {
        //        When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence
        _viewModel.latitude.value = userLocation.latitude
        _viewModel.longitude.value = userLocation.longitude
        findNavController().popBackStack()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // TODO: Change the map type based on the user's selection.
        R.id.normal_map -> {
            true
        }
        R.id.hybrid_map -> {
            true
        }
        R.id.satellite_map -> {
            true
        }
        R.id.terrain_map -> {
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    @SuppressLint("MissingPermission")
    private fun fetchingUserLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                try {
                    userLocation = it
                    latitude = it.latitude.toString()
                    longitude = it.longitude.toString()
                    moveCamera(LatLng(it.latitude, it.longitude), DEFAULT_ZOOM, "My Location")
                } catch (e: Exception) {
                    userLocation = Location("")
                    Log.d("TAG", "Location error ${e.message}")
                }
            } else {
                Log.d("TAG", "onComplete: current location is null")
                view?.let { it1 ->
                    Snackbar.make(
                        it1,
                        "Yours location is not retrieved! Try again.", Snackbar.LENGTH_LONG
                    ).setAction("Retry") {
                        getMyLocation()
                    }.show()
                }
            }
        }
    }
    private fun getMyLocation() {
        isMyLocationSet = false
        mMap.setOnMyLocationChangeListener {
            try {
                if (isMyLocationSet)
                    return@setOnMyLocationChangeListener
                isMyLocationSet = true
                userLocation = it
                Log.d("TAG", "current location ${it.latitude} and latitude ${it.longitude}")
                moveCamera(LatLng(it.latitude, it.longitude), DEFAULT_ZOOM, "Location")
            } catch (e: Exception) {
                Log.d("TAG", "Location: ${e.message}")
            }
        }
    }
    private fun moveCamera(latLng: LatLng, zoom: Float, title: String) {
        mMap.clear()
        Log.d(
            "TAG",
            "moveCamera: moving the camera to: lat: $latLng.latitude, lng: $latLng.longitude"
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(title)
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        )
        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {}
            override fun onMarkerDrag(marker: Marker) {}
            override fun onMarkerDragEnd(marker: Marker) {
                Log.d(
                    "TAG",
                    "onMarkerDragEnd: $marker.position.latitude,  $marker.position.longitude"
                )
                userLocation.latitude = marker.position.latitude
                userLocation.longitude = marker.position.longitude
            }
        })
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && permissions.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                fetchingUserLocation()
            } else {
                val rational =
                    shouldShowRequestPermissionRationale(permissions[0]) && shouldShowRequestPermissionRationale(
                        permissions[1]
                    )
                if (!rational) {
                    AlertDialog.Builder(requireActivity())
                        .setTitle("Permission required!")
                        .setMessage("This permission is essential to proceed further.")
                        .setPositiveButton("OK", object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                val uri: Uri =
                                    Uri.fromParts("package", requireActivity().packageName, null)
                                intent.data = uri
                                startActivity(intent)
                            }
                        }).setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                dialog?.dismiss()
                            }

                        }).show()
                } else {
                    getRuntimePermissions()
                }
            }
        }
    }

    private fun startOperation() {
        binding.proceed.setOnClickListener {
            var bundle=Bundle()
            bundle.putDouble("latitude", userLocation.latitude)
            bundle.putDouble("longitude", userLocation.longitude)
        //    findNavController().navigate(bundle)
        }
    }

}
