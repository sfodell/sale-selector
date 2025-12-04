package com.cs407.saleselector.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    onBack: () -> Unit
){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Account", style = MaterialTheme.typography.displayLarge)
                },
                navigationIcon = {
                    IconButton(onClick = onBack){
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            // Use onPrimary color for icons on a primary background
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    // Use the primary color (dark blue) for the app bar background
                    containerColor = MaterialTheme.colorScheme.primary,
                    // Use onPrimary color (white) for the title text
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->

        // The Scaffold automatically sets its background to MaterialTheme.colorScheme.background
        // which you defined as your cream color.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // This text will use onBackground (black) on a background (cream) color
            Text(
                "Email: user@example.com",
                style = MaterialTheme.typography.bodyLarge // Uses "Raleway" font
            )
            Button(
                onClick = {
                    //implement sign out
                },
                modifier = Modifier.fillMaxWidth(),
                // Customize button colors using ButtonDefaults
                colors = ButtonDefaults.buttonColors(
                    // Use secondary color (light blue) for the button background
                    containerColor = MaterialTheme.colorScheme.secondary,
                    // Use onSecondary color (black) for the text inside the button
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                // This will now use your custom "Raleway" font (labelLarge style)
                Text("Sign Out", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
