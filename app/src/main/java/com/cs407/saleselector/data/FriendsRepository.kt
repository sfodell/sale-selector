package com.cs407.saleselector.data

import android.util.Log
import com.cs407.saleselector.ui.model.FriendStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

object FriendsRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    //search
    suspend fun searchUserByEmail(email: String): FriendStatus? {
        return try {
            val querySnapshot = db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val doc = querySnapshot.documents[0]
                if (doc.id == auth.currentUser?.uid) return null

                val rawName = doc.getString("name")
                val safeName = if (rawName.isNullOrEmpty()) {
                    email.substringBefore("@")
                } else {
                    rawName
                }

                FriendStatus(
                    userID = doc.id,
                    name = safeName,
                    active = false,
                    // salesVisited removed
                    status = "none"
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FriendsRepo", "Error searching user", e)
            null
        }
    }

    //friend requests
    suspend fun sendFriendRequest(targetUser: FriendStatus) {
        val currentUserId = auth.currentUser?.uid ?: return
        val currentUserEmail = auth.currentUser?.email ?: ""

        val displayName = auth.currentUser?.displayName
        val currentUserName = if (!displayName.isNullOrBlank()) {
            displayName
        } else {
            currentUserEmail.substringBefore("@")
        }

        try {
            //add to users list
            val myFriendEntry = FriendStatus(
                userID = targetUser.userID,
                name = targetUser.name,
                active = false,
                // salesVisited removed
                status = "pending_sent"
            )

            db.collection("users").document(currentUserId)
                .collection("friends").document(targetUser.userID)
                .set(myFriendEntry).await()

            //add to target list
            val theirFriendEntry = FriendStatus(
                userID = currentUserId,
                name = currentUserName,
                active = false,
                // salesVisited removed
                status = "pending_received"
            )
            db.collection("users").document(targetUser.userID)
                .collection("friends").document(currentUserId)
                .set(theirFriendEntry).await()

        } catch (e: Exception) {
            Log.e("FriendsRepo", "Error sending request", e)
        }
    }

    //accept a request
    suspend fun acceptFriendRequest(requester: FriendStatus) {
        val currentUserId = auth.currentUser?.uid ?: return
        try {
            //update BOTH sides to "accepted"

            //user
            val myUpdate = mapOf("status" to "accepted")
            db.collection("users").document(currentUserId)
                .collection("friends").document(requester.userID)
                .set(myUpdate, SetOptions.merge()).await()

            //target
            val theirUpdate = mapOf("status" to "accepted")
            db.collection("users").document(requester.userID)
                .collection("friends").document(currentUserId)
                .set(theirUpdate, SetOptions.merge()).await()

        } catch (e: Exception) {
            Log.e("FriendsRepo", "Error accepting request", e)
        }
    }

    //remove a friend
    suspend fun removeFriend(friendId: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        try {
            db.collection("users").document(currentUserId)
                .collection("friends").document(friendId).delete().await()

            db.collection("users").document(friendId)
                .collection("friends").document(currentUserId).delete().await()
        } catch (e: Exception) {
            Log.e("FriendsRepo", "Error removing friend", e)
        }
    }

    //getters
    suspend fun getMyFriends(): List<FriendStatus> {
        val currentUserId = auth.currentUser?.uid ?: return emptyList()
        return try {
            val snapshot = db.collection("users").document(currentUserId)
                .collection("friends")
                .whereEqualTo("status", "accepted")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(FriendStatus::class.java)?.copy(userID = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getIncomingRequests(): List<FriendStatus> {
        val currentUserId = auth.currentUser?.uid ?: return emptyList()
        return try {
            val snapshot = db.collection("users").document(currentUserId)
                .collection("friends")
                .whereEqualTo("status", "pending_received")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(FriendStatus::class.java)?.copy(userID = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    //live listener for requests
    fun addRequestsListener(onUpdate: (List<FriendStatus>) -> Unit) {
        val currentUserId = auth.currentUser?.uid ?: return

        db.collection("users").document(currentUserId)
            .collection("friends")
            .whereEqualTo("status", "pending_received")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("FriendsRepo", "Listen failed", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val requests = snapshot.documents.mapNotNull { doc ->
                        val obj = doc.toObject(FriendStatus::class.java)
                        obj?.copy(userID = doc.id)
                    }
                    onUpdate(requests)
                } else {
                    onUpdate(emptyList())
                }
            }
    }

    // Simple listener for friends list
    fun addFriendsListener(onUpdate: (List<FriendStatus>) -> Unit) {
        val currentUserId = auth.currentUser?.uid ?: return

        db.collection("users").document(currentUserId)
            .collection("friends")
            .whereEqualTo("status", "accepted")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("FriendsRepo", "Error listening to friends list", e)
                    return@addSnapshotListener
                }

                val friends = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(FriendStatus::class.java)?.copy(userID = doc.id, active = false)
                } ?: emptyList()

                onUpdate(friends)
            }
    }
}
