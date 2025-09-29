package com.example.prog7314progpoe.database.user

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object FirebaseUserDbHelper {

    private val db = FirebaseDatabase
        .getInstance("https://chronosync-f3425-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("Users")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun registerUser(
        email: String,
        password: String,
        firstName: String,
        surname: String,
        dateOfBirth: String,
        location: String,
        onComplete: (Boolean, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val userMap = mapOf(
                        "email" to email,
                        "firstName" to firstName,
                        "surname" to surname,
                        "dateOfBirth" to dateOfBirth,
                        "location" to location
                    )

                    db.child(uid).setValue(userMap)
                        .addOnSuccessListener {
                            onComplete(true, "Registration successful")
                        }
                        .addOnFailureListener { ex ->
                            onComplete(false, "Error saving user data: ${ex.message}")
                        }
                } else {
                    onComplete(false, "Registration failed: ${task.exception?.message}")
                }
            }
    }

    // Login with Firebase Auth
    fun loginUser(
        email: String,
        password: String,
        onComplete: (success: Boolean, message: String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, "Login successful")
                } else {
                    onComplete(false, "Login failed: ${task.exception?.message}")
                }
            }
    }

    fun getUser(userEmail: String, callback: (UserModel?) -> Unit) {
        db.child(userEmail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserModel::class.java)?.apply {
                    this.email = snapshot.key ?: ""
                }
                callback(user)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    fun getAllUsers(callback: (List<UserModel>) -> Unit) {
        db.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<UserModel>()
                snapshot.children.forEach {
                    it.getValue(UserModel::class.java)?.let { user -> list.add(user) }
                }
                callback(list)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    fun updateUser(userId: String, user: UserModel, callback: (Boolean) -> Unit) {
        db.child(userId)

        db.setValue(user)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun deleteUser(userId: String, onComplete: () -> Unit = {}) {
        db.child(userId).removeValue()
            .addOnCompleteListener { task ->
            onComplete()
            }
    }
}