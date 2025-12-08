package com.cs407.saleselector.data

import com.cs407.saleselector.ui.model.Sale
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object SaleRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Add a sale for the current user
    suspend fun addSale(sale: Sale): Result<String> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Not logged in"))
            val saleWithUser = sale.copy(userId = userId)

            val docRef = db.collection("sales").add(saleWithUser).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get all sales for the current user
    suspend fun getUserSales(): Result<List<Sale>> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Not logged in"))

            val snapshot = db.collection("sales")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val sales = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Sale::class.java)?.copy(id = doc.id)
            }

            Result.success(sales)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get all sales (for the map view)
    suspend fun getAllSales(): Result<List<Sale>> {
        return try {
            val snapshot = db.collection("sales").get().await()

            val sales = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Sale::class.java)?.copy(id = doc.id)
            }

            Result.success(sales)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // Delete a sale
    suspend fun deleteSale(saleId: String): Result<Unit> {
        return try {
            db.collection("sales").document(saleId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAllUserSales(userId: String): Result<Unit> {
        return try {
            val snapshot = db.collection("sales")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            // Delete all documents in a batch
            val batch = db.batch()
            snapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            batch.commit().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}