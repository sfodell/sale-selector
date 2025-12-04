package com.cs407.saleselector.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.cs407.saleselector.ui.model.Sale
import com.cs407.saleselector.ui.theme.CustomColors


@Composable
fun SaleCard(sale: Sale, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "${sale.city} • ${sale.type}",
                style = MaterialTheme.typography.bodyLarge,
                color = colorResource(id = com.cs407.saleselector.R.color.dark_blue)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = sale.host, style = MaterialTheme.typography.bodyMedium, color = colorResource(id = com.cs407.saleselector.R.color.dark_blue))

            Text(
                text = sale.address,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}