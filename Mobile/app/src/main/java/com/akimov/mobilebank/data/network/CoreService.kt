package com.akimov.mobilebank.data.network

import com.akimov.mobilebank.data.models.BankAccountNetwork
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID

interface CoreService {
    @GET("/api/bank-accounts/owner/{id}")
    suspend fun getAccounts(
        @Path("id")
        id: UUID
    ): Response<List<BankAccountNetwork>>


    @POST("/api/bank-accounts/open")
    suspend fun createAccount(
        @Body
        name: String
    ): BankAccountNetwork
}