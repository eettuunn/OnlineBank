package com.akimov.mobilebank.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.akimov.mobilebank.data.database.entity.CreditEntity
import com.akimov.mobilebank.data.database.entity.LoanRateEntity
import java.util.UUID

@Dao
interface CreditsDao {
    @Query("SELECT * FROM credits")
    suspend fun getLoansList(): List<CreditEntity>

    @Query("SELECT * FROM credits WHERE id=:id")
    suspend fun findCreditById(id: UUID): CreditEntity?

    @Upsert
    fun insertCredit(dbEntity: CreditEntity)

    @Query("SELECT * FROM loan_rates")
    suspend fun getRates(): List<LoanRateEntity>

    @Upsert
    suspend fun insertRate(dbEntity: LoanRateEntity)

    @Query("SELECT * FROM loan_rates WHERE id=:id")
    fun findRateById(id: String): LoanRateEntity?
}