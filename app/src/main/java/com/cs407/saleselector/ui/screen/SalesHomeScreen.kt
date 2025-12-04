package com.cs407.saleselector.ui.screen

import android.Manifest
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cs407.saleselector.R
import com.cs407.saleselector.data.SaleRepository
import com.cs407.saleselector.ui.components.SaleCard
import com.cs407.saleselector.ui.model.Sale
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
){
    var allSales by remember { mutableStateOf<List<Sale>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            val result = SaleRepository.getAllSales()
            result.onSuccess { sales ->
                allSales = sales
            }
        }
    }


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
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(43.0731, -89.4012), 14f)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.home_title), style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(onClick = onOpenFriends, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.btn_friends_list))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(onClick = onOpenMySales, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.btn_my_sales))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(onClick = onOpenAccount, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.btn_account))
                    }
                }
            }
        }
    ) {
        Scaffold { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Map content takes up the whole screen
                SalesMapContent(
                    cameraPositionState = cameraPositionState,
                    hasLocationPermission = hasLocationPermission
                )

                // FABs and other UI elements are layered on top of the map
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start
                ) {
                    FloatingActionButton(
                        onClick = { scope.launch { drawerState.open() } },
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.btn_friends_list))
                    }

                    // Bottom content alignment depends on orientation
                    val configuration = LocalConfiguration.current
                    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

                    if (isLandscape) {
                        // In landscape, group buttons on the right
                        Column(horizontalAlignment = Alignment.End) {
                            Button(onClick = { showSheet = true }, modifier = Modifier.fillMaxWidth(0.4f)) {
                                Text(stringResource(R.string.btn_show_nearby))
                            }
                            Spacer(Modifier.height(8.dp))
                            ExtendedFloatingActionButton(
                                onClick = onOpenMyRoute,
                                modifier = Modifier.fillMaxWidth(0.4f)
                            ) {
                                Text(stringResource(R.string.btn_my_route))
                            }
                        }
                    } else {
                        // In portrait, stack buttons at the bottom
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Button(onClick = { showSheet = true }, modifier = Modifier.fillMaxWidth()) {
                                Text(stringResource(R.string.btn_show_nearby))
                            }
                            Spacer(Modifier.height(8.dp))
                            ExtendedFloatingActionButton(onClick = onOpenMyRoute) {
                                Text(stringResource(R.string.btn_my_route))
                            }
                        }
                    }
                }
            }
        }
    }


    if (showSheet){
        ModalBottomSheet(onDismissRequest = {showSheet = false}) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.sheet_nearby_title), style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(SaleStore.sales) {sale -> SaleCard(sale) }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

//Extracted Map Logic to reuse in both layouts
@Composable
fun SalesMapContent(
    cameraPositionState: com.google.maps.android.compose.CameraPositionState,
    hasLocationPermission: Boolean
) {
    val uiSettings = remember {
        MapUiSettings(myLocationButtonEnabled = true)
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
