package com.cs407.saleselector.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cs407.saleselector.R
import com.cs407.saleselector.data.FriendsRepository
import com.cs407.saleselector.ui.components.AddFriendDialog
import com.cs407.saleselector.ui.components.FriendDetailDialog
import com.cs407.saleselector.ui.model.FriendStatus
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    //states
    var friendsList by remember { mutableStateOf<List<FriendStatus>>(emptyList()) }
    var requestsList by remember { mutableStateOf<List<FriendStatus>>(emptyList()) }

    LaunchedEffect(Unit) {
        //ensure user document exists so requests can be received

        //listen for friends list updates
        FriendsRepository.addFriendsListener { updatedList ->
            friendsList = updatedList
        }

        //listen for incoming requests (REAL-TIME)
        FriendsRepository.addRequestsListener { updatedRequests ->
            requestsList = updatedRequests
        }
    }

    //Search States
    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var searchResult by remember { mutableStateOf<FriendStatus?>(null) }
    var isAlreadyFriend by remember { mutableStateOf(false) }
    var isSearching by remember { mutableStateOf(false) }

    //Detail States
    var selectedFriend by remember { mutableStateOf<FriendStatus?>(null) }
    var showDetailDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.friends_screen_title),
                        style = MaterialTheme.typography.displayLarge,
                        color = colorResource(id = R.color.white)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back_arrow),
                            tint = colorResource(id = R.color.white)
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
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                //friend requests
                if (requestsList.isNotEmpty()) {
                    item {
                        Text(
                            text = "Friend Requests",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(requestsList) { request ->
                        FriendRequestItem(
                            name = request.name,
                            onAccept = {
                                scope.launch {
                                    FriendsRepository.acceptFriendRequest(request)
                                }
                            },
                            onDecline = {
                                scope.launch {
                                    FriendsRepository.removeFriend(request.userID)
                                }
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                //my friends
                item {
                    Text(
                        text = "My Friends",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                        color = colorResource(id = R.color.white)
                    )
                }

                items(friendsList) { friend ->
                    FriendListItem(
                        name = friend.name,
                        isActive = friend.active,
                        onClick = {
                            selectedFriend = friend
                            showDetailDialog = true
                        }
                    )
                }

                if (friendsList.isEmpty()) {
                    item {
                        Text(
                            text = "No friends added yet.",
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            //search
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 24.dp)
            ) {
                Text(
                    text = "Add Friend by Email",
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
                        placeholder = { Text("Enter email address") },
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
                            val query = searchQuery.trim()
                            if (query.isNotEmpty()) {
                                isSearching = true
                                scope.launch {
                                    val foundUser = FriendsRepository.searchUserByEmail(query)
                                    searchResult = foundUser

                                    if (foundUser != null) {
                                        val isFriend = friendsList.any { it.userID == foundUser.userID }
                                        val isPending = requestsList.any { it.userID == foundUser.userID }
                                        isAlreadyFriend = isFriend || isPending
                                    }

                                    isSearching = false
                                    showDialog = true
                                }
                            }
                        },
                        enabled = !isSearching,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.dark_blue),
                        )
                    ) {
                        Text(
                            text = if (isSearching) "..." else stringResource(R.string.btn_search),
                            color = colorResource(id = android.R.color.white)
                        )
                    }
                }
            }
        }

        //dialogs
        if (showDialog) {
            AddFriendDialog(
                isOpen = showDialog,
                onDismiss = { showDialog = false },
                searchedUser = searchResult,
                isAlreadyFriend = isAlreadyFriend,
                onAddFriend = {
                    if (searchResult != null) {
                        scope.launch {
                            FriendsRepository.sendFriendRequest(searchResult!!)
                            showDialog = false
                            searchQuery = ""
                        }
                    }
                }
            )
        }

        if (showDetailDialog && selectedFriend != null) {
            FriendDetailDialog(
                friend = selectedFriend,
                isOpen = showDetailDialog,
                onDismiss = { showDetailDialog = false },
                onRemoveFriend = { friendToRemove ->
                    scope.launch {
                        FriendsRepository.removeFriend(friendToRemove.userID)
                        showDetailDialog = false
                    }
                }
            )
        }
    }
}

//helpers
@Composable
fun FriendRequestItem(
    name: String,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.white)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (name.isBlank()) "Unknown User" else name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Sent a friend request",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                //Decline Button
                Button(
                    onClick = onDecline,
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.delete_red)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Decline",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                //Accept Button
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.status_online)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Accept",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FriendListItem(
    name: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (name.isBlank()) "Unknown" else name,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )

            //Status Dot WI
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = if (isActive) colorResource(R.color.status_online) else colorResource(R.color.status_offline),
                        shape = CircleShape
                    )
            )
        }
    }
}
