package com.akimov.mobilebank.data.network

import com.akimov.mobilebank.data.models.BankAccountListNetwork
import com.akimov.mobilebank.data.models.BankAccountNetwork
import com.akimov.mobilebank.data.models.ChangeNameUpload
import com.akimov.mobilebank.data.models.CreateAccountUpload
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface CoreService {
    @GET("/api/bank-accounts/owner/{id}")
    suspend fun getAccounts(
        @Path("id")
        id: UUID,

        @Query("creationDateSortDirection")
        creationDateSortDirection: String = "ASC",

        @Query("isClosed")
        isClosed: Boolean = false,

        @Query("pageNumber")
        pageNumber: Int = 1,

        @Query("pageSize")
        pageSize: Int = 10
    ): Response<BankAccountListNetwork>


    @POST("/api/bank-accounts/open")
    suspend fun createAccount(
        @Body
        createAccountUpload: CreateAccountUpload
    ): BankAccountNetwork

    @POST("/api/bank-accounts/{id}/deposit")
    suspend fun deposit(
        amount: Long,

        @Path("id")
        accountID: String,

        @Body
        transactionType: String
    )

    @POST("/api/bank-accounts/{id}/withDraw")
    suspend fun withdraw(
        amount: Long,

        @Path("id")
        accountID: String,

        @Body
        transactionType: String
    )

    @PUT("/api/bank-accounts/{id}/name")
    suspend fun changeName(
        @Path("id")
        accountID: String,
        @Body
        changeNameUpload: ChangeNameUpload
    )
}