package com.akimov.mobilebank.data.network

import com.akimov.mobilebank.data.models.CreditNetwork
import com.akimov.mobilebank.data.models.GetLoanUpload
import com.akimov.mobilebank.data.models.LoanRate
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LoanService {
    @GET("loan_api/loan/{userId}")
    suspend fun getLoansList(
        @Path("userId")
        userId: String
    ): Response<List<CreditNetwork>>

    @POST("loan_api/loan")
    suspend fun getLoan(
        @Body
        getLoanUpload: GetLoanUpload
    )

    @GET("loan_api/rate")
    suspend fun getLoanRates(): List<LoanRate>
}