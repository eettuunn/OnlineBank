package com.akimov.mobilebank.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.akimov.mobilebank.data.database.entity.BankAccountEntity
import com.akimov.mobilebank.data.database.entity.CreditEntity
import com.akimov.mobilebank.data.database.entity.LoanRateEntity
import com.akimov.mobilebank.data.database.entity.OperationEntity

@Database(
    entities = [
        BankAccountEntity::class, CreditEntity::class,
        LoanRateEntity::class, OperationEntity::class
    ],
    version = 4,
    autoMigrations = [
    ]
)
abstract class RoomDb : RoomDatabase() {
    abstract fun accountsDao(): AccountsDao
    abstract fun creditsDao(): CreditsDao
    abstract fun operationsDao(): OperationsDao
}
