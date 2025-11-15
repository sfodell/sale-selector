package com.cs407.saleselector.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cs407.saleselector.ui.components.SaleCard
import com.cs407.saleselector.ui.model.SaleStore
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesHomeScreen(
    onOpenMyRoute: () -> Unit,
    onOpenFriends: () -> Unit,
    onOpenMySales: () -> Unit,
    onOpenAccount: () -> Unit,
){
    val locations = listOf("Verona", "Monona", "Shorewood Hills", "Madison", "Sun Prairie")
    var showSheet by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(43.0731, -89.4012), 14f)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {Text("SaleSelector", style = MaterialTheme.typography.headlineLarge)},
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = Color.Transparent) {
                Spacer(Modifier.weight(1f))
                ExtendedFloatingActionButton(onClick = onOpenMyRoute) {
                    Text("My Route")
                }
            }
        }
    ) { paddingValues ->
        Column(
          modifier = Modifier
              .fillMaxSize()
              .padding(paddingValues)
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ){
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){
                OutlinedButton(onClick = onOpenFriends) {
                    Text("Friends List")
                }
                OutlinedButton(onClick = onOpenMySales) {
                    Text("My Sales")
                }
                OutlinedButton(onOpenAccount) {
                    Text("Account")
                }
            }
            Button(onClick = {showSheet = true}, modifier = Modifier.fillMaxWidth()){
                Text("Show more sales nearby")
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ){
                items(SaleStore.sales.take(3)) {sale -> SaleCard(sale) }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                )
            }
        }


        if (showSheet){
            ModalBottomSheet(onDismissRequest = {showSheet = false}) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nearby Sales", style = MaterialTheme.typography.titleLarge)
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