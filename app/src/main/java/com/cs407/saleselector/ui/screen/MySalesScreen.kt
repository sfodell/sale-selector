package com.cs407.saleselector.ui.screen

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.cs407.saleselector.data.SaleRepository
import com.cs407.saleselector.ui.components.SaleCard
import com.cs407.saleselector.ui.model.Sale
import com.cs407.saleselector.ui.model.SaleStore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySalesScreen(
    onBack: () -> Unit,
    onAddSale: () -> Unit
){
    //place holders
    var mySales by remember { mutableStateOf(SaleStore.sales.take(2)) }
//    var isLoading by remember { mutableStateOf(true) }
//    val scope = rememberCoroutineScope()

    // Load user's sales when screen opens
//    LaunchedEffect(Unit) {
//        scope.launch {
//            val result = SaleRepository.getUserSales()
//            result.onSuccess { sales ->
//                mySales = sales
//            }
//            isLoading = false
//        }
//    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Sales", style = MaterialTheme.typography.displayLarge, color = colorResource(id = com.cs407.saleselector.R.color.white)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = colorResource(id = com.cs407.saleselector.R.color.white))
                    }
                },
                actions = {
                    IconButton(onClick = onAddSale){
                        Icon(Icons.Filled.Add, null, tint = colorResource(id = com.cs407.saleselector.R.color.white))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = colorResource(id = com.cs407.saleselector.R.color.light_blue)
    ){ paddingValues ->
        //       if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

        }
        //      } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(mySales) { sale: Sale ->
                SaleCard(sale)
            }
        }
        //  }
    }
}