package com.cs407.saleselector.ui.screen

import android.content.Context
import android.location.Geocoder
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.cs407.saleselector.ui.model.Sale
import com.cs407.saleselector.ui.model.SaleStore
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSaleScreen(
    onBack: () -> Unit,
    onSave: () -> Unit,
){
    var city by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("")}
    var host by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Add Sale", style = MaterialTheme.typography.displayLarge, color = colorResource(id = com.cs407.saleselector.R.color.white))
                },
                navigationIcon = {
                    IconButton(onClick = onBack){
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = colorResource(id = com.cs407.saleselector.R.color.white))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = colorResource(id = com.cs407.saleselector.R.color.light_blue)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = city,
                onValueChange = {city = it},
                label = {Text("City")},
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(id = com.cs407.saleselector.R.color.white),
                    unfocusedBorderColor = colorResource(id = com.cs407.saleselector.R.color.dark_blue),
                    focusedLabelColor = colorResource(id = com.cs407.saleselector.R.color.white),
                    unfocusedLabelColor = colorResource(id = com.cs407.saleselector.R.color.dark_blue),
                    cursorColor = colorResource(id = com.cs407.saleselector.R.color.white),
                    focusedTextColor = colorResource(id = com.cs407.saleselector.R.color.white),
                    unfocusedTextColor = colorResource(id = com.cs407.saleselector.R.color.white)
                )
            )
            OutlinedTextField(
                value = type,
                onValueChange = {type = it},
                label = {Text("Type (Garage, Yard, etc.)")},
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(id = com.cs407.saleselector.R.color.white),
                    unfocusedBorderColor = colorResource(id = com.cs407.saleselector.R.color.dark_blue),
                    focusedLabelColor = colorResource(id = com.cs407.saleselector.R.color.white),
                    unfocusedLabelColor = colorResource(id = com.cs407.saleselector.R.color.dark_blue),
                    cursorColor = colorResource(id = com.cs407.saleselector.R.color.white),
                    focusedTextColor = colorResource(id = com.cs407.saleselector.R.color.white),
                    unfocusedTextColor = colorResource(id = com.cs407.saleselector.R.color.white)
                )
            )
            OutlinedTextField(
                value = host,
                onValueChange = {host = it},
                label = {Text("Name - Contact Info")},
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(id = com.cs407.saleselector.R.color.white),
                    unfocusedBorderColor = colorResource(id = com.cs407.saleselector.R.color.dark_blue),
                    focusedLabelColor = colorResource(id = com.cs407.saleselector.R.color.white),
                    unfocusedLabelColor = colorResource(id = com.cs407.saleselector.R.color.dark_blue),
                    cursorColor = colorResource(id = com.cs407.saleselector.R.color.white),
                    focusedTextColor = colorResource(id = com.cs407.saleselector.R.color.white),
                    unfocusedTextColor = colorResource(id = com.cs407.saleselector.R.color.white)
                )
            )
            OutlinedTextField(
                value = address,
                onValueChange = {address = it},
                label = {Text("Address")},
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(id = com.cs407.saleselector.R.color.white),
                    unfocusedBorderColor = colorResource(id = com.cs407.saleselector.R.color.dark_blue),
                    focusedLabelColor = colorResource(id = com.cs407.saleselector.R.color.white),
                    unfocusedLabelColor = colorResource(id = com.cs407.saleselector.R.color.dark_blue),
                    cursorColor = colorResource(id = com.cs407.saleselector.R.color.white),
                    focusedTextColor = colorResource(id = com.cs407.saleselector.R.color.white),
                    unfocusedTextColor = colorResource(id = com.cs407.saleselector.R.color.white)
                )
            )

            if (error != null) {
                Text(text = error!!, color = Color.Red)
            }

            Button(
                onClick = {
                    scope.launch {
                        isSaving = true
                        error = null

                        val fullAddress = "$address, $city"
                        val geocoder = Geocoder(context, Locale.getDefault())

                        val result = geocoder.getFromLocationName(fullAddress, 1)

                        if (!result.isNullOrEmpty()) {
                            val location = result[0]

                            SaleStore.sales.add(
                                Sale(
                                    city = city,
                                    type = type,
                                    host = host,
                                    address = address,
                                    lat = location.latitude,
                                    lng = location.longitude
                                )
                            )
                        } else {
                            // Fallback if geocoder fails
                            SaleStore.sales.add(
                                Sale(
                                    city = city,
                                    type = type,
                                    host = host,
                                    address = address,
                                    lat = 0.0,
                                    lng = 0.0
                                )
                            )
                        }
                    }

                    onSave()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = com.cs407.saleselector.R.color.white)
                )
            ) {
                Text("Save", color = colorResource(id = com.cs407.saleselector.R.color.dark_blue))
            }

        }
    }
}