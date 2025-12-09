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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import com.cs407.saleselector.data.SaleRepository
import kotlinx.coroutines.tasks.await


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit
){
    var isDeleting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Account", style = MaterialTheme.typography.displayLarge, color = colorResource(id = com.cs407.saleselector.R.color.white))
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
            val currentUser = Firebase.auth.currentUser
            val userEmail = currentUser?.email ?: "No email found"

            Text("Email: $userEmail", color = colorResource(id = com.cs407.saleselector.R.color.white))

            Button(
                onClick = {
                    // Sign out
                    Firebase.auth.signOut()
                    onLogout() // Navigate to login page
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = com.cs407.saleselector.R.color.white)
                )
            ) {
                Text("Sign Out", color = colorResource(id = com.cs407.saleselector.R.color.dark_blue))
            }

            Button(
                onClick = {
                    scope.launch {
                        isDeleting = true
                        errorMessage = null

                        try {
                            val user = Firebase.auth.currentUser
                            val userId = user?.uid

                            if (userId != null && user != null) {
                                // Delete all user sales first
                                val deleteSalesResult = SaleRepository.deleteAllUserSales(userId)

                                deleteSalesResult.onSuccess {
                                    // Delete the user account using await to properly handle the async operation
                                    try {
                                        user.delete().await()
                                        // Sign out and navigate to login
                                        Firebase.auth.signOut()
                                        onLogout()
                                    } catch (e: Exception) {
                                        errorMessage = "Failed to delete account: ${e.message}"
                                        isDeleting = false
                                    }
                                }.onFailure { exception ->
                                    errorMessage = "Failed to delete sales: ${exception.message}"
                                    isDeleting = false
                                }
                            } else {
                                errorMessage = "No user logged in"
                                isDeleting = false
                            }
                        } catch (e: Exception) {
                            errorMessage = "Error: ${e.message}"
                            isDeleting = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isDeleting,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                Text(if (isDeleting) "Deleting..." else "Delete Account")
            }
        }
    }
}