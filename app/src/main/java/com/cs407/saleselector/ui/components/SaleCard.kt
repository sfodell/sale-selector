package com.cs407.saleselector.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cs407.saleselector.ui.model.Sale
import com.cs407.saleselector.ui.theme.CustomColors


@Composable
fun SaleCard(sale: Sale, modifier: Modifier = Modifier){
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = CustomColors.accent)
    ) {
        Column(Modifier.padding(16.dp)){
            Text("${sale.city} - ${sale.type}", style = MaterialTheme.typography.titleLarge)
            Text(sale.host)
            Text(sale.address)
        }
    }
}