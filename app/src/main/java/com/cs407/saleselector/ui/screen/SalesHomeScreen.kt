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
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cs407.saleselector.R
import com.cs407.saleselector.ui.components.PrimaryButton
import com.cs407.saleselector.ui.components.SaleCard
import com.cs407.saleselector.ui.components.SecondaryButton
import com.cs407.saleselector.ui.model.SaleStore
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesHomeScreen(
    onOpenMyRoute: () -> Unit,
    onOpenFriends: () -> Unit,
    onOpenMySales: () -> Unit,
    onOpenAccount: () -> Unit,
){
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

    //Determine orientation
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            //In landscape smaller bar
            if (!isLandscape) {
                CenterAlignedTopAppBar(
                    title = {Text(stringResource(R.string.home_title), style = MaterialTheme.typography.headlineLarge)},
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        },
        bottomBar = {
            //Only show bottom bar in portrait otherwise it eats up space
            if (!isLandscape) {
                BottomAppBar(containerColor = Color.Transparent) {
                    Spacer(Modifier.weight(1f))
                    ExtendedFloatingActionButton(onClick = onOpenMyRoute) {
                        Text(stringResource(R.string.btn_my_route))
                    }
                }
            }
        }
    ) { paddingValues ->

        if (isLandscape) {
            //Landscape
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                //Left Side now holds the buttons
                Column(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.home_title), style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(16.dp))

                    SecondaryButton(
                        text = stringResource(R.string.btn_friends_list),
                        onClick = onOpenFriends
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SecondaryButton(
                        text = stringResource(R.string.btn_my_sales),
                        onClick = onOpenMySales
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SecondaryButton(
                        text = stringResource(R.string.btn_account),
                        onClick = onOpenAccount
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    PrimaryButton(
                        text = stringResource(R.string.btn_show_nearby),
                        onClick = { showSheet = true }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ExtendedFloatingActionButton(
                        onClick = onOpenMyRoute,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.btn_my_route))
                    }
                }

                //Right Side: Map
                Box(
                    modifier = Modifier
                        .weight(0.6f) // Takes 60% width
                        .fillMaxHeight()
                ) {
                    SalesMapContent(
                        cameraPositionState = cameraPositionState,
                        hasLocationPermission = hasLocationPermission
                    )
                }
            }
        } else {
            //Portrait
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ){
                    SecondaryButton(
                        text = stringResource(R.string.btn_friends_list),
                        onClick = onOpenFriends,
                        modifier = Modifier.weight(1f)
                    )
                    SecondaryButton(
                        text = stringResource(R.string.btn_my_sales),
                        onClick = onOpenMySales,
                        modifier = Modifier.weight(1f)
                    )
                    SecondaryButton(
                        text = stringResource(R.string.btn_account),
                        onClick = onOpenAccount,
                        modifier = Modifier.weight(1f)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    SalesMapContent(
                        cameraPositionState = cameraPositionState,
                        hasLocationPermission = hasLocationPermission
                    )
                }

                PrimaryButton(
                    text = stringResource(R.string.btn_show_nearby),
                    onClick = { showSheet = true }
                )
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
