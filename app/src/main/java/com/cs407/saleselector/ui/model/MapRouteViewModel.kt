package com.cs407.saleselector.ui.model

import androidx.lifecycle.ViewModel
import com.cs407.saleselector.ui.model.Sale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapRouteViewModel : ViewModel() {

    private val _selectedSales = MutableStateFlow<Set<String>>(emptySet())
    val selectedSales = _selectedSales.asStateFlow()

    fun toggleSaleSelection(saleId: String) {
        _selectedSales.value = _selectedSales.value.toMutableSet().also {
            if (it.contains(saleId)) it.remove(saleId)
            else it.add(saleId)
        }
    }

    fun isSelected(saleId: String): Boolean {
        return _selectedSales.value.contains(saleId)
    }
}
