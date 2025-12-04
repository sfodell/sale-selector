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
                    active = doc.getBoolean("active") ?: false,
                    salesVisted = (doc.getLong("salesVisited") ?: 0).toInt(),
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
                salesVisted = targetUser.salesVisted,
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
                salesVisted = 0,
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

    //live listener
    fun addRequestsListener(onUpdate: (List<FriendStatus>) -> Unit) {
        val currentUserId = auth.currentUser?.uid ?: return

        Log.d("FriendsRepo", "Attaching requests listener for $currentUserId")

        db.collection("users").document(currentUserId)
            .collection("friends")
            .whereEqualTo("status", "pending_received")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("FriendsRepo", "Listen failed", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    Log.d("FriendsRepo", "Found ${snapshot.documents.size} requests")
                    val requests = snapshot.documents.mapNotNull { doc ->
                        // Force the userID to match the document ID to prevent data mismatch
                        val obj = doc.toObject(FriendStatus::class.java)
                        obj?.copy(userID = doc.id)
                    }
                    onUpdate(requests)
                } else {
                    Log.d("FriendsRepo", "No requests found")
                    onUpdate(emptyList())
                }
            }
    }

    //status WIP
    fun setMyStatus(isOnline: Boolean) {
        val currentUserId = auth.currentUser?.uid ?: return
        val userEmail = auth.currentUser?.email ?: ""
        val userName = auth.currentUser?.displayName.let {
            if (it.isNullOrBlank()) userEmail.substringBefore("@") else it
        }

        val statusMap = mapOf(
            "active" to isOnline,
            "email" to userEmail,
            "name" to userName
        )

        db.collection("users").document(currentUserId)
            .set(statusMap, SetOptions.merge())
            .addOnFailureListener { e -> Log.e("FriendsRepo", "Error setting status", e) }
    }

    //live friends listener
    fun addFriendsListener(onUpdate: (List<FriendStatus>) -> Unit) {
        val currentUserId = auth.currentUser?.uid ?: return

        db.collection("users").document(currentUserId)
            .collection("friends")
            .whereEqualTo("status", "accepted")
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                val subcollectionFriends = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(FriendStatus::class.java)?.copy(userID = doc.id)
                } ?: emptyList()

                val friendIds = subcollectionFriends.map { it.userID }

                if (friendIds.isNotEmpty()) {
                    db.collection("users")
                        .whereIn(com.google.firebase.firestore.FieldPath.documentId(), friendIds)
                        .addSnapshotListener { userSnap, userErr ->
                            if (userErr != null) {
                                // Fallback to basic list if root query fails
                                onUpdate(subcollectionFriends)
                                return@addSnapshotListener
                            }

                            val rootUsersMap = userSnap?.documents?.associate { doc ->
                                doc.id to doc.toObject(FriendStatus::class.java)
                            } ?: emptyMap()

                            val mergedList = subcollectionFriends.map { basicFriend ->
                                val liveData = rootUsersMap[basicFriend.userID]
                                if (liveData != null) {
                                    // Merge live status with basic info
                                    basicFriend.copy(
                                        active = liveData.active,
                                        salesVisted = liveData.salesVisted
                                    )
                                } else {
                                    basicFriend
                                }
                            }
                            onUpdate(mergedList)
                        }
                } else {
                    onUpdate(emptyList())
                }
            }
    }
}
