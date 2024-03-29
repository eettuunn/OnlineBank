package com.akimov.mobilebank.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.akimov.mobilebank.data.database.entity.BankAccountEntity
import java.util.UUID

@Dao
interface AccountsDao {
    @Query("SELECT * FROM bank_accounts")
    suspend fun getAccounts(): List<BankAccountEntity>

    @Upsert
    suspend fun insertAccount(account: BankAccountEntity)

    @Query("SELECT * FROM bank_accounts WHERE id=:id")
    suspend fun findAccountById(id: UUID): BankAccountEntity?
}