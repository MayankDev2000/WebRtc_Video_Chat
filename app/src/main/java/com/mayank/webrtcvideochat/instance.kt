package com.mayank.webrtcvideochat

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

object instance {
    val firebaseRDB = FirebaseDatabase.getInstance().getReference("users")
    var firebaseuserRDB:DatabaseReference = firebaseRDB
}