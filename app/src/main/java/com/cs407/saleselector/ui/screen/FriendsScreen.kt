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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.Dialog
import com.cs407.saleselector.R
import com.cs407.saleselector.data.FriendsRepository
import com.cs407.saleselector.data.SaleRepository
import com.cs407.saleselector.ui.components.AddFriendDialog
import com.cs407.saleselector.ui.components.SaleCard
import com.cs407.saleselector.ui.model.FriendStatus
import com.cs407.saleselector.ui.model.Sale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    onBack: () -> Unit,
    onGoToSale: (Sale) -> Unit // New callback for navigation
) {
    val scope = rememberCoroutineScope()

    //states
    var friendsList by remember { mutableStateOf<List<FriendStatus>>(emptyList()) }
    var requestsList by remember { mutableStateOf<List<FriendStatus>>(emptyList()) }

    LaunchedEffect(Unit) {
        FriendsRepository.addFriendsListener { updatedList ->
            friendsList = updatedList
        }
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

    //Detail & Sales States
    var selectedFriend by remember { mutableStateOf<FriendStatus?>(null) }
    var selectedFriendSales by remember { mutableStateOf<List<Sale>>(emptyList()) }
    var isLoadingSales by remember { mutableStateOf(false) }
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
                        isActive = false,
                        onClick = {
                            selectedFriend = friend
                            showDetailDialog = true
                            // Fetch sales for this friend
                            scope.launch {
                                isLoadingSales = true
                                val result = SaleRepository.getSalesByUserId(friend.userID)
                                selectedFriendSales = result.getOrNull() ?: emptyList()
                                isLoadingSales = false
                            }
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

        // New Dialog that shows sales
        if (showDetailDialog && selectedFriend != null) {
            FriendSalesDialog(
                friend = selectedFriend!!,
                sales = selectedFriendSales,
                isLoading = isLoadingSales,
                onDismiss = { showDetailDialog = false },
                onRemove = {
                    scope.launch {
                        FriendsRepository.removeFriend(selectedFriend!!.userID)
                        showDetailDialog = false
                    }
                },
                onGoToSale = { sale ->
                    showDetailDialog = false
                    onGoToSale(sale)
                }
            )
        }
    }
}

// Replaces the old FriendDetailDialog with one that shows sales
@Composable
fun FriendSalesDialog(
    friend: FriendStatus,
    sales: List<Sale>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onRemove: () -> Unit,
    onGoToSale: (Sale) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = colorResource(id = R.color.white),
            modifier = Modifier
                .fillMaxWidth()
                .height(550.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                // Header
                Text(
                    text = friend.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = colorResource(id = R.color.dark_blue),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Active Sales",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Sales List
                Box(modifier = Modifier.weight(1f)) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else if (sales.isEmpty()) {
                        // Polished Empty State
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No active sales",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Gray
                            )
                            Text(
                                text = "Check back later!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray.copy(alpha = 0.7f)
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(sales) { sale ->
                                Box(modifier = Modifier.clickable { onGoToSale(sale) }) {
                                    SaleCard(
                                        sale = sale,
                                        onDelete = null
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onRemove) {
                        Text("Remove Friend", color = Color.Red)
                    }
                    TextButton(onClick = onDismiss) {
                        Text("Close", color = colorResource(id = R.color.dark_blue))
                    }
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
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular Avatar with Initial
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(colorResource(id = R.color.white), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.firstOrNull()?.toString()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleMedium,
                    color = colorResource(id = R.color.dark_blue),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Friend Name
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                color = colorResource(id = R.color.white),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

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
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    color = colorResource(id = R.color.white)
                )
                Text(
                    text = "Wants to be friends",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorResource(id = R.color.white).copy(alpha = 0.7f)
                )
            }

            // Action Buttons
            Row {
                IconButton(onClick = onAccept) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Accept",
                        tint = Color.Green
                    )
                }
                IconButton(onClick = onDecline) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Decline",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}
