package com.cs407.saleselector.ui.model

import android.content.Context
import androidx.compose.runtime.mutableStateListOf

/**
 * Represents a Garage Sale.
 * Default values are required for Firebase's toObject() deserialization.
 */
data class Sale(
    val id: String = "",
    val userId: String = "",
    val city: String = "",
    val type: String = "",
    val host: String = "",
    val address: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0
)

object SaleStore {
    // Used by SalesHomeScreen to display pins
    val sales = mutableStateListOf<Sale>()
}

/**
 * Represents a User/Friend.
 * Default values are required for Firebase's toObject() deserialization.
 */
data class FriendStatus(
    val userID: String = "",
    val name: String = "",
    val active: Boolean = false,
    val salesVisted: Int = 0,
    val status: String = "accepted"
)

/**
 * Legacy local storage.
 * We are switching to FriendsRepository (Firebase), so the logic here
 * is emptied out to ensure we don't accidentally use local data.
 */
object FriendsStore {
    val friends = mutableStateListOf<FriendStatus>()

    // Placeholders to prevent crash if called from non-updated screens
    fun loadFriends(context: Context) {
        // No-op: Friends are now loaded via FriendsRepository.getMyFriends()
    }

    fun addFriend(context: Context, friend: FriendStatus) {
        // No-op: Use FriendsRepository.addFriend()
    }

    fun removeFriend(context: Context, friend: FriendStatus) {
        // No-op: Use FriendsRepository.removeFriend()
    }
}

/**
 * Legacy mock users.
 * Emptied to force usage of Firebase search.
 */
object UserStore {
    val allUsers = listOf<FriendStatus>()
}
