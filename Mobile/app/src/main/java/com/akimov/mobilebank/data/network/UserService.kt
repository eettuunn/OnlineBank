package com.akimov.mobilebank.data.network

import com.akimov.mobilebank.data.models.IdModel
import com.akimov.mobilebank.data.models.SendingEmail
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("/user_api/user/login")
    suspend fun login(
        @Body
        email: SendingEmail
    ): IdModel
}

