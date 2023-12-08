package com.esh7enly.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.esh7enly.domain.entity.userservices.Provider

@Dao
interface ProviderDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(provider: Provider)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(provider: List<Provider>?)


    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(provider: Provider)

    @Query("SELECT * from Provider WHERE category_id =:id ORDER BY sort asc")
    fun getProviders(id:Int):LiveData<List<Provider>>

    @Query("SELECT count(*) FROM Provider")
    suspend fun getProviderCount():Int

    @Query("SELECT * FROM Provider WHERE id =:id")
    fun getProvider(id:Int):Provider

    @Query("DELETE FROM Provider")
    fun deleteProviders()
}