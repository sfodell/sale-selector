package com.cs407.saleselector.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cs407.saleselector.R
import com.cs407.saleselector.ui.components.AddFriendDialog
import com.cs407.saleselector.ui.components.FriendDetailDialog
import com.cs407.saleselector.ui.model.FriendStatus
import com.cs407.saleselector.ui.model.FriendsStore
import com.cs407.saleselector.ui.model.UserStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activeFriends = FriendsStore.friends.filter { it.active }
    val inactiveFriends = FriendsStore.friends.filter { !it.active }

    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var searchResult by remember { mutableStateOf<FriendStatus?>(null) }
    var isAlreadyFriend by remember { mutableStateOf(false) }

    var selectedFriend by remember { mutableStateOf<FriendStatus?>(null) }
    var showDetailDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.friends_screen_title),
                        style = MaterialTheme.typography.displayLarge,
                        color = colorResource(id = com.cs407.saleselector.R.color.white)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back_arrow),
                            tint = colorResource(id = com.cs407.saleselector.R.color.white)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = colorResource(id = R.color.light_blue)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            //Friends List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.status_online),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                        color = colorResource(id = R.color.white)
                    )
                }
                items(activeFriends) { friend ->
                    FriendListItem(name = friend.name, isActive = true,
                        onClick = {
                            selectedFriend = friend
                            showDetailDialog = true
                        }
                    )
                }
                item {
                    Text(
                        text = stringResource(R.string.status_offline),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                        color = colorResource(id = R.color.white)

                    )
                }
                items(inactiveFriends) { friend ->
                    FriendListItem(name = friend.name, isActive = false,
                        onClick = {
                            selectedFriend = friend
                            showDetailDialog = true
                        }
                    )
                }
            }

            //Bottom Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 24.dp)
            ) {
                Text(
                    text = stringResource(R.string.add_friends_header),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = colorResource(id = R.color.white)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text(stringResource(R.string.search_placeholder)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(id = R.color.white),
                            unfocusedBorderColor = colorResource(id = R.color.dark_blue),
                            cursorColor = colorResource(id = R.color.white),
                            focusedTextColor = colorResource(id = R.color.white),
                            unfocusedTextColor = colorResource(id = R.color.white),
                            focusedLabelColor = colorResource(id = R.color.white),
                            unfocusedLabelColor = colorResource(id = R.color.white),
                            disabledPlaceholderColor = colorResource(id = R.color.white),
                            focusedPlaceholderColor = colorResource(id = R.color.white),
                            unfocusedPlaceholderColor = colorResource(id = R.color.dark_blue)

                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            //User info, name or username case insensitive
                            val query = searchQuery.trim()
                            val foundUser = UserStore.allUsers.find {
                                it.name.equals(query, ignoreCase = true) ||
                                        it.userID.equals(query, ignoreCase = true)
                            }
                            searchResult = foundUser

                            //Check if already friend
                            if (foundUser != null) {
                                isAlreadyFriend = FriendsStore.friends.any { it.userID == foundUser.userID }
                            } else {
                                isAlreadyFriend = false
                            }

                            //Show dialog
                            showDialog = true
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.dark_blue),

                            )
                    ) {
                        Text(stringResource(R.string.btn_search),
                            color = colorResource(id = android.R.color.white))
                    }
                }
            }
        }

        //Popup for add friend
        if (showDialog) {
            AddFriendDialog(
                isOpen = showDialog,
                onDismiss = { showDialog = false },
                searchedUser = searchResult,
                isAlreadyFriend = isAlreadyFriend,
                onAddFriend = {
                    //Add friend to friends list
                    if (searchResult != null) {
                        FriendsStore.addFriend(context, searchResult!!)
                        showDialog = false
                        searchQuery = ""
                    }
                }
            )
        }
        //Popup for detail/remove
        if (showDetailDialog) {
            FriendDetailDialog(
                friend = selectedFriend,
                isOpen = showDetailDialog,
                onDismiss = { showDetailDialog = false },
                onRemoveFriend = { friendToRemove ->
                    FriendsStore.removeFriend(context, friendToRemove)
                    showDetailDialog = false
                }
            )
        }
    }
}

@Composable
fun FriendListItem(name: String, isActive: Boolean, onClick: () -> Unit) {
    //Fetch colors from resources
    val activeColor = colorResource(R.color.status_online)
    val inactiveColor = colorResource(R.color.status_offline)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge
            )

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = if (isActive) activeColor else inactiveColor,
                        shape = CircleShape
                    )
            )
        }
    }
}
