package com.cs407.saleselector.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cs407.saleselector.R
import com.cs407.saleselector.ui.model.FriendStatus

@Composable
fun AddFriendDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    searchedUser: FriendStatus?,
    isAlreadyFriend: Boolean,
    onAddFriend: () -> Unit
) {
    val redColor = colorResource(R.color.delete_red)

    if (isOpen) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_cancel),
                                tint = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    //No user
                    if (searchedUser == null) {
                        Text(
                            text = stringResource(R.string.no_user_found),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                    }
                    //Found user
                    else {
                        Text(
                            text = searchedUser.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        //Already friend
                        if (isAlreadyFriend) {
                            Text(
                                text = stringResource(R.string.user_already_friend),
                                style = MaterialTheme.typography.bodyMedium,
                                color = redColor
                            )
                        }
                        //Exists but not friend
                        else {
                            Button(
                                onClick = onAddFriend,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth(0.6f)
                            ) {
                                Text(stringResource(R.string.btn_add_friend))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
