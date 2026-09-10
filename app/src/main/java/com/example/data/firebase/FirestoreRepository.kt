package com.example.data.firebase

import android.util.Log
import com.example.data.model.ServiceRequestEntity
import com.example.data.model.UserEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Handles cloud data persistence with Firestore to keep track of user data,
 * user requests, and synchronizing between cloud and local cache.
 */
class FirestoreRepository(private val context: android.content.Context? = null) {
    private val TAG = "FirestoreRepository"
    private val firestore: FirebaseFirestore? get() = FirebaseManager.getFirestore(context)

    /**
     * Persists or updates user profile in Firestore under /users/{userId}
     */
    suspend fun saveUserProfile(
        userId: String,
        displayName: String,
        email: String,
        phone: String,
        role: String,
        wilaya: String,
        commune: String,
        exactAddress: String,
        authProvider: String = "GOOGLE"
    ): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore not available"))

        return try {
            val userData = hashMapOf(
                "userId" to userId,
                "displayName" to displayName,
                "email" to email,
                "phone" to phone,
                "role" to role,
                "wilaya" to wilaya,
                "commune" to commune,
                "exactAddress" to exactAddress,
                "authProvider" to authProvider,
                "updatedAt" to System.currentTimeMillis(),
                "platform" to "WASTA_ANDROID_DZ"
            )

            db.collection("users")
                .document(userId)
                .set(userData, SetOptions.merge())
                .await()

            Log.i(TAG, "Successfully synced user profile to Firestore: $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving user profile to Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Persists user's service request to Firestore under /users/{userId}/requests/{requestId}
     * and global /service_requests/{requestId}
     */
    suspend fun saveServiceRequest(userId: String, request: ServiceRequestEntity): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore not available"))

        return try {
            val requestData = hashMapOf(
                "id" to request.id,
                "customerId" to userId,
                "customerName" to request.customerName,
                "customerPhone" to request.customerPhone,
                "problemDescription" to request.problemDescription,
                "category" to request.category,
                "service" to request.service,
                "specialtyNeeded" to request.specialtyNeeded,
                "urgency" to request.urgency,
                "wilaya" to request.wilaya,
                "commune" to request.commune,
                "approximateArea" to request.approximateArea,
                "status" to request.status,
                "assignedProfessionalId" to (request.assignedProfessionalId ?: ""),
                "createdAt" to request.createdAt,
                "syncedToFirestoreAt" to System.currentTimeMillis()
            )

            // Save in user's subcollection
            db.collection("users")
                .document(userId)
                .collection("requests")
                .document(request.id)
                .set(requestData, SetOptions.merge())
                .await()

            // Also mirror in root service_requests for professional dispatch
            db.collection("service_requests")
                .document(request.id)
                .set(requestData, SetOptions.merge())
                .await()

            Log.i(TAG, "Service request ${request.id} persisted to Firestore")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving service request to Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Saves user's favorite professional to Firestore
     */
    suspend fun toggleFavoritePro(userId: String, proId: String, proName: String, isFavorite: Boolean): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore not available"))

        return try {
            val docRef = db.collection("users")
                .document(userId)
                .collection("favorites")
                .document(proId)

            if (isFavorite) {
                val data = hashMapOf(
                    "proId" to proId,
                    "proName" to proName,
                    "addedAt" to System.currentTimeMillis()
                )
                docRef.set(data).await()
            } else {
                docRef.delete().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error toggling favorite in Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Real-time stream of user profile data from Firestore
     */
    fun observeUserProfile(userId: String): Flow<Map<String, Any>?> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val registration = db.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed for user profile", error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    trySend(snapshot.data)
                } else {
                    trySend(null)
                }
            }

        awaitClose { registration.remove() }
    }

    /**
     * Real-time stream of user requests from Firestore
     */
    fun observeUserRequests(userId: String): Flow<List<Map<String, Any>>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val registration = db.collection("users")
            .document(userId)
            .collection("requests")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed for requests", error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { it.data } ?: emptyList()
                trySend(items)
            }

        awaitClose { registration.remove() }
    }
}
