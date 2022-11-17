package com.gta.data.source

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.gta.data.model.UserInfo
import javax.inject.Inject

class LoginDataSource @Inject constructor(
    databaseReference: DatabaseReference
) {

    private val userDatabaseReference: DatabaseReference = databaseReference.child("users")

    fun getUser(uid: String): Task<DataSnapshot> = userDatabaseReference.child(uid).get()

    fun createUser(uid: String): Task<Void> =
        userDatabaseReference.child(uid).setValue(UserInfo(chatId = uid.hashCode().toLong()))
}
