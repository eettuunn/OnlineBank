package com.akimov.mobilebank.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.akimov.mobilebank.data.database.entity.OperationEntity

@Dao
interface OperationsDao {

    @Query("SELECT * FROM operations WHERE id=:id")
    suspend fun findOperationById(id: String): OperationEntity?

    @Upsert
    suspend fun insertOperation(dbEntity: OperationEntity)

    @Query("SELECT * FROM operations")
    abstract fun getOperations(): List<OperationEntity>
}