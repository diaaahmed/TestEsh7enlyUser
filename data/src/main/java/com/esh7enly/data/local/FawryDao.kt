package com.esh7enly.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.esh7enly.domain.entity.FawryEntity

@Dao
interface FawryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(fawryEntity: FawryEntity)

    @Query("SELECT * From FawryEntity")
    fun getFawryOperations():LiveData<List<FawryEntity>>

    @Query("DELETE FROM FawryEntity where FawryEntity.id == :id")
    fun deleteFawryOperations(id:Int)

    @Query("DELETE FROM FawryEntity")
    fun clearFawryOperations()
}