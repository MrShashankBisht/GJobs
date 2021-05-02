package com.iab.GJobs.services

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class FirebaseTokenService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        val deviceToken = FirebaseInstanceId.getInstance().getToken()
        Log.d("FirebaseTokenService", "onTokenRefresh: $deviceToken")
    }
}