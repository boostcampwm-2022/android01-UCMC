package com.gta.presentation.util

import com.google.firebase.auth.FirebaseUser

object FirebaseUtil {
    var uid = ""
        private set

    fun setUid(firebaseUser: FirebaseUser) {
        uid = firebaseUser.uid
    }
}
