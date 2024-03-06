package com.akimov.mobilebank.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.akimov.mobilebank.data.database.entity.BankAccountEntity

@Database(entities = [BankAccountEntity::class], version = 1)
abstract class RoomDb : RoomDatabase() {
    abstract fun accountsDao(): AccountsDao
}