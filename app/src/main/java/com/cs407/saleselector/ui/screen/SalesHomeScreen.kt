package com.cs407.saleselector.ui.screen

import android.Manifest
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cs407.saleselector.R
import com.cs407.saleselector.ui.components.SaleCard
import com.cs407.saleselector.ui.model.SaleStore
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesHomeScreen(
    onOpenMyRoute: () -> Unit,
    onOpenFriends: () -> Unit,
    onOpenMySales: () -> Unit,
    onOpenAccount: () -> Unit,
) {
    var hasLocationPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    var showSheet by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(43.0731, -89.4012), 14f)
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Menu, contentDescription = null) },
                    label = { Text(stringResource(R.string.btn_friends_list)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onOpenFriends()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text(stringResource(R.string.btn_my_sales)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onOpenMySales()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                    label = { Text(stringResource(R.string.btn_account)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onOpenAccount()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.home_title),
                        style = MaterialTheme.typography.displayLarge) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    },
                    // Make the app bar semi-transparent to see the map behind it
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                    )
                )
            }
        ) { paddingValues ->

            // The main content area now holds the map and the buttons overlaid on it.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Map takes up the entire background
                SalesMapContent(
                    cameraPositionState = cameraPositionState,
                    hasLocationPermission = hasLocationPermission
                )

                // UI elements are placed on top of the map using the Box layout
                if (isLandscape) {
                    // Landscape layout with buttons on the side
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(200.dp), // Fixed width for the button column
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            ExtendedFloatingActionButton(
                                onClick = onOpenMyRoute,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.btn_my_route))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showSheet = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.btn_show_nearby))
                            }
                        }
                    }
                } else {
                    // Portrait layout with buttons at the bottom
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { showSheet = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.btn_show_nearby))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        ExtendedFloatingActionButton(onClick = onOpenMyRoute) {
                            Text(stringResource(R.string.btn_my_route))
                        }
                    }
                }
            }

            if (showSheet) {
                ModalBottomSheet(onDismissRequest = { showSheet = false }) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            stringResource(R.string.sheet_nearby_title),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(Modifier.height(8.dp))
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(SaleStore.sales) { sale -> SaleCard(sale) }
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

// Extracted Map Logic to reuse in both layouts
@Composable
fun SalesMapContent(
    cameraPositionState: com.google.maps.android.compose.CameraPositionState,
    hasLocationPermission: Boolean
) {
    val uiSettings = remember {
        MapUiSettings(myLocationButtonEnabled = true, zoomControlsEnabled = false) // Disabled zoom controls to avoid overlap
    }
    val properties = remember(hasLocationPermission) {
        MapProperties(isMyLocationEnabled = hasLocationPermission)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = uiSettings,
        properties = properties
    ) {
        SaleStore.sales.forEach { sale ->
            Marker(
                state = rememberMarkerState(position = LatLng(sale.lat, sale.lng)),
                title = sale.type,
                snippet = sale.host
            )
        }
    }
}
