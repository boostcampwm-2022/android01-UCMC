package com.gta.data.source

import com.google.firebase.database.DatabaseReference
import com.gta.data.model.UserInfo
import javax.inject.Inject

class LoginDataSource @Inject constructor(
    databaseReference: DatabaseReference
) {

    private val userDatabaseReference = databaseReference.child("user")

    fun getUser(uid: String) = userDatabaseReference.child(uid).get()

    fun createUser(uid: String) =
        userDatabaseReference.child(uid).setValue(UserInfo(chatId = uid.hashCode().toLong()))
}
