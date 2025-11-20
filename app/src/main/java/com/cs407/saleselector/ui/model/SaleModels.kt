package com.cs407.saleselector.ui.model

import androidx.compose.runtime.mutableStateListOf

data class Sale(
    val city: String,
    val type: String,
    val host: String,
    val address: String,
    val lat: Double,
    val lng: Double
)

object SaleStore {
    val sales = mutableStateListOf<Sale>()
}

data class FriendStatus(
    val name: String,
    val active: Boolean,
)

object FriendsStore{
    val friends = listOf(
        FriendStatus("Kateri", true),
        FriendStatus("Lily", false),
        FriendStatus("Carlos", true)
    )
}