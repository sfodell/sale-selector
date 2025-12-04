package com.cs407.saleselector.ui.model

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


data class Sale(
    val id: String = "", // Add unique ID for each sale
    val userId: String = "", // Firebase user ID
    val city: String = "",
    val type: String = "",
    val host: String = "",
    val address: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0
)

object SaleStore {
    val sales = mutableStateListOf<Sale>()
}

data class FriendStatus(
    val userID: String,
    val name: String,
    val active: Boolean,
    val salesVisted: Int
)

object FriendsStore {
    //Load from disk
    val friends = mutableStateListOf<FriendStatus>()

    private const val PREFS_NAME = "FriendPrefs"
    private const val FRIENDS_KEY = "saved_friends"

    //Called when app starts
    fun loadFriends(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(FRIENDS_KEY, null)

        friends.clear()
        if (json != null) {
            val type = object : TypeToken<List<FriendStatus>>() {}.type
            val savedList: List<FriendStatus> = Gson().fromJson(json, type)
            friends.addAll(savedList)
        } else {
            //Default data if nothing is saved yet
            friends.addAll(
                listOf()
            )
        }
    }

    //Friend added
    fun addFriend(context: Context, friend: FriendStatus) {
        friends.add(friend)
        saveFriends(context)
    }

    //Remove friend
    fun removeFriend(context: Context, friend: FriendStatus) {
        friends.remove(friend)
        saveFriends(context)
    }

    private fun saveFriends(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(friends)
        editor.putString(FRIENDS_KEY, json)
        editor.apply()
    }
}

object UserStore {
    val allUsers = listOf(
        FriendStatus("user1", "Kateri", true, 2),
        FriendStatus("user2", "Lily", false, 14),
        FriendStatus("user3", "Carlos", true, 23),
        FriendStatus("user4", "Sam", true, 5),
        FriendStatus("user5", "Austin", false, 7),
        FriendStatus("user6", "Kalp", true, 9),
        FriendStatus("user7", "Michael", false, 10),
        FriendStatus("user8", "Gavin", false, 9)
    )
}
