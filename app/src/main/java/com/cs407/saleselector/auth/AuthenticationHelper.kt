package com.cs407.saleselector.auth

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest


// ============================================
// Email Validation
// ============================================

enum class EmailResult {
    Valid,
    Empty,
    Invalid,
}

fun validateEmail(email: String): EmailResult {
    if (email.isEmpty()){
        return EmailResult.Empty
    }

    // 1. username of email should only contain "0-9, a-z, _, A-Z, ."
    // 2. there is one and only one "@" between username and server address
    // 3. there are multiple domain names with at least one top-level domain
    // 4. domain name "0-9, a-z, -, A-Z" (could not have "_" but "-" is valid)
    // 5. multiple domain separate with '.'
    // 6. top level domain should only contain letters and at lest 2 letters
    // this email check only valid for this course
    val pattern = Regex("^[\\w.]+@([a-zA-Z0-9-]{2,}\\.)+[a-zA-Z]{2,}$")

    return if (pattern.matches(email)) {
        EmailResult.Valid
    } else {
        EmailResult.Invalid
    }
}

// ============================================
// Password Validation
// ============================================

enum class PasswordResult {
    Valid,
    Empty,
    Short,
    Invalid
}

fun validatePassword(password: String): PasswordResult {
    // 1. password should contain at least one uppercase letter, lowercase letter, one digit
    // 2. minimum length: 5
    if (password.isEmpty()) {
        return PasswordResult.Empty
    }
    if (password.length < 5) {
        return PasswordResult.Short
    }
    if (Regex("\\d+").containsMatchIn(password) &&
        Regex("[a-z]+").containsMatchIn(password) &&
        Regex("[A-Z]+").containsMatchIn(password)
    ) {
        return PasswordResult.Valid
    }
    return PasswordResult.Invalid

}

// ============================================
// Firebase Authentication Functions
// ============================================

/**
 * Sign in existing user with email and password
 * If sign-in fails, automatically attempts to create new account
 */
fun signIn(
    email: String,
    password: String,
    onResult: (success: Boolean, message: String?, uid: String?) -> Unit
    //any other callback function or parameters if you want
) {
    val auth = Firebase.auth
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success
                // Get current user from the response and propogate it
                val user = auth.currentUser
                if (user != null) {
                    onResult(true, null, user.uid)
                } else {
                    onResult(false, "Unknown error: User is null", null)
                }
            } else {
                // Sign in failed, try creating account
                // Call createAccount function
                createAccount(email, password) { success, message, uid ->
                    if (success) {
                        onResult(true, null, uid)
                    } else {
                        onResult(false, message, null)
                    }
                }
            }
        }
}

/**
 * Create new Firebase account with email and password
 */
fun createAccount(
    email: String,
    password: String,
    onResult: (success: Boolean, message: String?, uid: String?) -> Unit
    //any other callback function or parameters if you want
) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "createUserWithEmail:success")
                val user = auth.currentUser
                // Logic to propagate success response
                if (user != null) {
                    onResult(true, null, user.uid)
                } else {
                    onResult(false, "Unknown error: User is null", null)
                }
            } else {
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                // error in creation of account
                onResult(false, task.exception?.message ?: "Account creation failed", null)
            }
        }
}

/**
 * Update Firebase Auth displayName
 * Used in Milestone 3 for username collection
 */
fun updateName(name: String, onResult: (success: Boolean, message: String?) -> Unit) {
    val auth = Firebase.auth
    val user = auth.currentUser

    if (user != null) {
        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User profile updated.")
                    onResult(true, null)
                } else {
                    Log.w(TAG, "User profile update failed.", task.exception)
                    onResult(false, task.exception?.message ?: "Profile update failed")
                }
            }
    } else {
        onResult(false, "No user logged in")
    }
}