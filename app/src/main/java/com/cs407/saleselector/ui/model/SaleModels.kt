package com.cs407.saleselector.ui.model

data class Sale(
    val city: String,
    val type: String,
    val host: String,
    val address: String
)

object SaleStore{
    val sales = listOf(
        Sale("Verona", "Garage Sale", "Sam Odell - (123) 123-4567", "123 Oak St"),
        Sale("Monona", "Yard Sale", "Michael Kiem - (123) 123-4567", "44 Lake Ave"),
        Sale("Shorewood Hills", "Garage Sale", "Gavin Austin - (123) 123-4567", "9 Grove Ct"),
        Sale("Madison", "Garage Sale", "Kalp Patel - (123) 123-4567", "777 Monroe St"),
    )
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