package com.esh7enly.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.esh7enly.domain.entity.userservices.Service

@Dao
interface ServiceDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(service: Service)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(service: List<Service>?)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(service: Service)

    @Query("SELECT * from Service WHERE provider_id =:id ORDER BY sort asc")
    fun getServices(id:String):LiveData<List<Service>>

    //@Query("SELECT * from Service WHERE name_ar =:name ORDER BY sort asc")
    @Query("SELECT * FROM Service  WHERE name_ar || name_en LIKE '%' || :name || '%' ORDER BY sort ASC")
    fun searchService(name:String):LiveData<List<Service>>

    @Query("SELECT * FROM Service WHERE id IN(:ids) ORDER BY sort asc")
    fun getMultiServices(ids:List<Int>):List<Service>

    @Query("DELETE FROM Service")
    fun deleteServices()

    @Query("SELECT count(*) FROM Service")
    suspend fun getServiceCount():Int

}