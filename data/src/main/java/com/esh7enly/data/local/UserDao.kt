package com.esh7enly.data.local

import androidx.room.*
import com.esh7enly.domain.entity.VersionEntity

@Dao
interface UserDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(versionEntity: VersionEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(versionEntity: VersionEntity)

    @Query("SELECT * FROM VersionEntity")
    fun getVersion():VersionEntity

    @Query("SELECT service FROM VersionEntity")
    fun getVersionName():String

    @Query("SELECT service_update_num FROM VersionEntity")
    suspend fun getServiceUpdateNumber():String

    @Query("DELETE FROM VersionEntity")
    fun deleteVersion()

}