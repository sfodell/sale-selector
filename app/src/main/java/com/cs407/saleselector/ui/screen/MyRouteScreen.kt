package com.cs407.saleselector.ui.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.cs407.saleselector.data.SaleRepository
import com.cs407.saleselector.ui.model.MapRouteViewModel
import com.google.maps.android.compose.*
import com.cs407.saleselector.ui.model.Sale
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRouteScreen(
    onBack: () -> Unit,
    viewModel: MapRouteViewModel
) {
    val ctx = LocalContext.current
    val selectedSales by viewModel.selectedSales.collectAsState()
    val scope = rememberCoroutineScope()

    var allSales by remember { mutableStateOf<List<Sale>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                ctx,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (hasLocationPermission) {
            scope.launch {
                try {
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx)
                    val location = fusedLocationClient.lastLocation.await()
                    if (location != null) {
                        currentLocation = LatLng(location.latitude, location.longitude)
                    }
                } catch (e: SecurityException) {
                } catch (e: Exception) {
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true

            if (hasLocationPermission) {
                try {
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx)
                    val location = fusedLocationClient.lastLocation.await()
                    if (location != null) {
                        currentLocation = LatLng(location.latitude, location.longitude)
                    }
                } catch (e: SecurityException) {
                    hasLocationPermission = false
                } catch (e: Exception) {
                }
            }

            val result = SaleRepository.getAllSales()
            result.onSuccess { sales ->
                allSales = sales
                errorMessage = null
            }.onFailure { e ->
                errorMessage = "Failed to load sales: ${e.message}"
            }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("My Route", style = MaterialTheme.typography.headlineLarge)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            errorMessage ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(
                            onClick = {
                                scope.launch {
                                    isLoading = true
                                    val result = SaleRepository.getAllSales()
                                    result.onSuccess { sales ->
                                        allSales = sales
                                        errorMessage = null
                                    }.onFailure { e ->
                                        errorMessage = "Failed to load sales: ${e.message}"
                                    }
                                    isLoading = false
                                }
                            },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }
                allSales.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "No sales found",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            "Add some sales first to create a route",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (!hasLocationPermission) {
                            Button(
                                onClick = {
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text("Enable Location for Route Optimization")
                            }
                        }

                        Text(
                            text = "Tap markers to select sales for your route (${selectedSales.size} selected)",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(
                                currentLocation ?: LatLng(allSales.first().lat, allSales.first().lng),
                                11f
                            )
                        }

                        LaunchedEffect(allSales, currentLocation) {
                            if (allSales.isNotEmpty()) {
                                val bounds = com.google.android.gms.maps.model.LatLngBounds.builder()

                                currentLocation?.let { bounds.include(it) }

                                allSales.forEach { sale ->
                                    bounds.include(LatLng(sale.lat, sale.lng))
                                }
                                try {
                                    cameraPositionState.move(
                                        CameraUpdateFactory.newLatLngBounds(bounds.build(), 100)
                                    )
                                } catch (e: Exception) {
                                }
                            }
                        }

                        GoogleMap(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            cameraPositionState = cameraPositionState,
                            properties = MapProperties(
                                isMyLocationEnabled = hasLocationPermission
                            ),
                            uiSettings = MapUiSettings(
                                zoomControlsEnabled = true,
                                myLocationButtonEnabled = hasLocationPermission
                            )
                        ) {
                            currentLocation?.let { loc ->
                                Marker(
                                    state = MarkerState(position = loc),
                                    title = "Your Location",
                                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                                )
                            }

                            allSales.forEach { sale ->
                                val selected = viewModel.isSelected(sale.id)

                                Marker(
                                    state = MarkerState(position = LatLng(sale.lat, sale.lng)),
                                    title = sale.address,
                                    snippet = if (selected) "✓ Selected" else "Tap to select",
                                    onClick = {
                                        viewModel.toggleSaleSelection(sale.id)
                                        true
                                    },
                                    icon = if (selected) {
                                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                                    } else {
                                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                                    }
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (selectedSales.isNotEmpty()) {
                                    val sales = selectedSales.mapNotNull { id ->
                                        allSales.firstOrNull { it.id == id }
                                    }

                                    if (sales.isEmpty()) {
                                        Toast.makeText(ctx, "Error: Selected sales not found", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }

                                    val optimizedSales = optimizeRouteFromLocation(sales, currentLocation)
                                    val uri = buildGoogleMapsUri(optimizedSales, currentLocation)

                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                                    intent.setPackage("com.google.android.apps.maps")

                                    try {
                                        ctx.startActivity(intent)
                                    } catch (e: Exception) {
                                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                                        ctx.startActivity(browserIntent)
                                    }
                                } else {
                                    Toast.makeText(ctx, "Please select at least one sale", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = selectedSales.isNotEmpty(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text("Open Optimized Route in Google Maps")
                        }
                    }
                }
            }
        }
    }
}

fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val r = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLng / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
}

fun optimizeRouteFromLocation(sales: List<Sale>, currentLocation: LatLng?): List<Sale> {
    if (sales.isEmpty()) return emptyList()
    if (sales.size == 1) return sales

    val optimized = mutableListOf<Sale>()
    val remaining = sales.toMutableList()

    val startSale = if (currentLocation != null) {
        remaining.minByOrNull { sale ->
            calculateDistance(currentLocation.latitude, currentLocation.longitude, sale.lat, sale.lng)
        }!!
    } else {
        remaining.first()
    }

    remaining.remove(startSale)
    optimized.add(startSale)
    var current = startSale

    while (remaining.isNotEmpty()) {
        val nearest = remaining.minByOrNull { sale ->
            calculateDistance(current.lat, current.lng, sale.lat, sale.lng)
        }!!

        remaining.remove(nearest)
        optimized.add(nearest)
        current = nearest
    }

    return optimized
}

fun buildGoogleMapsUri(sales: List<Sale>, currentLocation: LatLng?): String {
    if (sales.isEmpty()) return ""

    val base = "https://www.google.com/maps/dir/?api=1"

    if (sales.size == 1) {
        val origin = if (currentLocation != null) {
            "&origin=${currentLocation.latitude},${currentLocation.longitude}"
        } else {
            ""
        }
        return "$base$origin&destination=${sales.first().lat},${sales.first().lng}&travelmode=driving"
    }

    val origin = if (currentLocation != null) {
        "${currentLocation.latitude},${currentLocation.longitude}"
    } else {
        "${sales.first().lat},${sales.first().lng}"
    }

    val destination = "${sales.last().lat},${sales.last().lng}"

    val waypointSales = if (currentLocation != null) {
        sales.dropLast(1)
    } else {
        sales.drop(1).dropLast(1)
    }

    val waypoints = if (waypointSales.isNotEmpty()) {
        waypointSales.joinToString("%7C") { "${it.lat},${it.lng}" }
    } else {
        ""
    }

    return if (waypoints.isNotEmpty()) {
        "$base&origin=$origin&destination=$destination&waypoints=$waypoints&travelmode=driving"
    } else {
        "$base&origin=$origin&destination=$destination&travelmode=driving"
    }
}