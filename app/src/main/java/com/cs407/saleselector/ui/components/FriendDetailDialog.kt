package com.cs407.saleselector.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.cs407.saleselector.ui.model.FriendStatus

@Composable
fun FriendDetailDialog(
    friend: FriendStatus?,
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onRemoveFriend: (FriendStatus) -> Unit
) {
    //Colors from resources
    val redColor = colorResource(R.color.delete_red)
    val grayColor = colorResource(R.color.status_offline)
    val activeColor = colorResource(R.color.status_online)
    val inactiveColor = colorResource(R.color.status_offline)
    val textSecondary = colorResource(R.color.text_secondary)
    val textHint = colorResource(R.color.text_hint)

    var showDeleteConfirmation by remember { mutableStateOf(false) }

    if (!isOpen) {
        showDeleteConfirmation = false
    }

    if (isOpen && friend != null) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxHeight(0.95f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = stringResource(R.string.cd_close)
                            )
                        }
                    }

                    if (showDeleteConfirmation) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(R.string.dialog_confirm_title),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.dialog_confirm_message, friend.name),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(24.dp))

                            //Confirm Button
                            Button(
                                onClick = {
                                    onRemoveFriend(friend)
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = redColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.btn_confirm_remove))
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            //Cancel Button
                            Button(
                                onClick = { showDeleteConfirmation = false },
                                colors = ButtonDefaults.buttonColors(containerColor = grayColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.btn_cancel))
                            }
                        }

                    } else {
                        //User
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = friend.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(
                                        color = if (friend.active) activeColor else inactiveColor,
                                        shape = CircleShape
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.stats_sales_visited, friend.salesVisted),
                                style = MaterialTheme.typography.titleMedium,
                                color = textSecondary
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = stringResource(R.string.badges_coming_soon),
                                style = MaterialTheme.typography.labelLarge,
                                color = textHint
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        //Remove Friend Button
                        Button(
                            onClick = { showDeleteConfirmation = true },
                            colors = ButtonDefaults.buttonColors(containerColor = redColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.btn_remove_friend))
                        }
                    }
                }
            }
        }
    }
}
