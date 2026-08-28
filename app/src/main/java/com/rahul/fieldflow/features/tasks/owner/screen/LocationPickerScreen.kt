package com.rahul.fieldflow.features.tasks.owner.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.rahul.fieldflow.features.tasks.model.SelectedLocation
import com.rahul.fieldflow.ui.theme.PrimaryBlue
import com.rahul.fieldflow.ui.theme.TextDark
import com.rahul.fieldflow.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerScreen(
    initialLocation: LatLng? = null,
    initialRadius: Int = 50,
    onBackClick: () -> Unit,
    onLocationConfirm: (SelectedLocation) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedLatLng by remember { mutableStateOf(initialLocation ?: LatLng(19.0760, 72.8777)) }
    var radiusMeters by remember { mutableFloatStateOf(initialRadius.coerceIn(50, 100).toFloat()) }
    var isMapLoading by remember { mutableStateOf(true) }
    var isLocating by remember { mutableStateOf(false) }
    var pendingMyLocationRequest by remember { mutableStateOf(false) }

    var mapInstance: MapLibreMap? by remember { mutableStateOf(null) }
    var mapViewInstance: MapView? by remember { mutableStateOf(null) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val moveToCurrentLocation = {
        if (!isLocationEnabled(context)) {
            scope.launch {
                snackbarHostState.showSnackbar("Turn on location services to use your current location.")
            }
        } else {
            val hasPermission = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                isLocating = true
                Log.d("LOCATION_PICKER_DEBUG", "location request started")
                getCurrentLocation(fusedLocationClient,
                    onLocationReceived = { latLng ->
                        isLocating = false
                        selectedLatLng = latLng
                        mapInstance?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0))
                        Log.d("LOCATION_PICKER_DEBUG", "latitude=${latLng.latitude}, longitude=${latLng.longitude}, camera moved, marker updated")
                    },
                    onError = {
                        isLocating = false
                        scope.launch {
                            snackbarHostState.showSnackbar("Unable to get your current location. Please try again.")
                        }
                    }
                )
            } else {
                pendingMyLocationRequest = true
                Log.d("LOCATION_PICKER_DEBUG", "permission not granted, requesting...")
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                      permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
        
        Log.d("LOCATION_PICKER_DEBUG", "permission state: granted=$granted")
        
        if (granted) {
            if (pendingMyLocationRequest || initialLocation == null) {
                pendingMyLocationRequest = false
                isLocating = true
                getCurrentLocation(fusedLocationClient,
                    onLocationReceived = { latLng ->
                        isLocating = false
                        selectedLatLng = latLng
                        mapInstance?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0))
                        Log.d("LOCATION_PICKER_DEBUG", "Auto location move after permission: lat=${latLng.latitude}, lng=${latLng.longitude}")
                    },
                    onError = {
                        isLocating = false
                    }
                )
            }
        } else {
            if (pendingMyLocationRequest) {
                pendingMyLocationRequest = false
                scope.launch {
                    snackbarHostState.showSnackbar("Location permission is required to use your current location.")
                }
            }
        }
    }

    // Launch permission request if pending
    LaunchedEffect(pendingMyLocationRequest) {
        if (pendingMyLocationRequest) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    LaunchedEffect(Unit) {
        if (initialLocation == null) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                getCurrentLocation(fusedLocationClient, { latLng ->
                    selectedLatLng = latLng
                    mapInstance?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0))
                })
            } else {
                permissionLauncher.launch(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                )
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Select Location", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Info dialog */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                modifier = Modifier.shadow(4.dp)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    MapLibre.getInstance(ctx.applicationContext)
                    MapView(ctx).also { mv ->
                        mapViewInstance = mv
                        mv.onCreate(null)
                        mv.getMapAsync { map ->
                            mapInstance = map
                            val styleUrl = "https://api.maptiler.com/maps/streets-v4/style.json?key=HVygLrZsaqc0hNoVzP7y"
                            map.setStyle(styleUrl) {
                                isMapLoading = false
                                map.cameraPosition = CameraPosition.Builder()
                                    .target(selectedLatLng)
                                    .zoom(if (initialLocation != null) 15.0 else 13.0)
                                    .build()
                            }
                            map.addOnCameraMoveListener {
                                if (!isLocating) {
                                    map.cameraPosition.target?.let { selectedLatLng = it }
                                }
                            }
                        }
                    }
                },
                update = {}
            )

            DisposableEffect(lifecycleOwner) {
                val observer = object : DefaultLifecycleObserver {
                    override fun onStart(owner: LifecycleOwner) { mapViewInstance?.onStart() }
                    override fun onResume(owner: LifecycleOwner) { mapViewInstance?.onResume() }
                    override fun onPause(owner: LifecycleOwner) { mapViewInstance?.onPause() }
                    override fun onStop(owner: LifecycleOwner) { mapViewInstance?.onStop() }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                    mapViewInstance?.onDestroy()
                    mapViewInstance = null
                    mapInstance = null
                }
            }

            CenterMarker(modifier = Modifier.align(Alignment.Center))

            // My Location FAB
            FloatingActionButton(
                onClick = {
                    Log.d("LOCATION_PICKER_DEBUG", "button clicked")
                    moveToCurrentLocation()
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 240.dp)
                    .size(52.dp),
                shape = CircleShape,
                containerColor = Color.White,
                contentColor = PrimaryBlue,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
            ) {
                if (isLocating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = PrimaryBlue
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "My location"
                    )
                }
            }

            if (isMapLoading) {
                Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.8f)), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = PrimaryBlue)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading map...", color = TextSecondary)
                    }
                }
            }

            BottomLocationPanel(
                radiusMeters = radiusMeters,
                onRadiusChange = { radiusMeters = it },
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                onConfirm = {
                    val finalRadius = radiusMeters.roundToInt().coerceIn(50, 100)
                    Log.d("LOCATION_PICKER_DEBUG", "confirm coordinates: lat=${selectedLatLng.latitude}, lng=${selectedLatLng.longitude}, radius=$finalRadius")
                    onLocationConfirm(
                        SelectedLocation(
                            latitude = selectedLatLng.latitude,
                            longitude = selectedLatLng.longitude,
                            radiusMeters = finalRadius,
                            address = null // Do not put raw coordinates in the human-readable address field
                        )
                    )
                }
            )
        }
    }
}

private fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
    return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
           locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
}

@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
    onLocationReceived: (LatLng) -> Unit,
    onError: () -> Unit = {}
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            onLocationReceived(LatLng(location.latitude, location.longitude))
        } else {
            // Last location might be null, try requesting a fresh one
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { freshLocation ->
                if (freshLocation != null) {
                    onLocationReceived(LatLng(freshLocation.latitude, freshLocation.longitude))
                } else {
                    onError()
                }
            }.addOnFailureListener {
                onError()
            }
        }
    }.addOnFailureListener {
        onError()
    }
}

@Composable
private fun CenterMarker(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.offset(y = (-24).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(48.dp).shadow(8.dp, CircleShape),
            shape = CircleShape,
            color = Color.White
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Box(modifier = Modifier.size(4.dp).background(PrimaryBlue, CircleShape))
    }
}

@Composable
private fun BottomLocationPanel(
    radiusMeters: Float,
    onRadiusChange: (Float) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = PrimaryBlue.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "Select Task Location", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(text = "Move the map to position the marker", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }

            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Geofence Radius", style = MaterialTheme.typography.labelMedium, color = TextDark)
                    Text("${radiusMeters.roundToInt()}m", style = MaterialTheme.typography.labelMedium, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = radiusMeters,
                    onValueChange = { onRadiusChange(it.coerceIn(50f, 100f)) },
                    valueRange = 50f..100f,
                    steps = 9,
                    colors = SliderDefaults.colors(thumbColor = PrimaryBlue, activeTrackColor = PrimaryBlue)
                )
            }

            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirm Location", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
