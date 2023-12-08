package com.esh7enly.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.esh7enly.domain.entity.userservices.Image
import com.esh7enly.domain.entity.userservices.Parameter


@Dao
interface ParameterDao {

    @Query("DELETE From Parameter")
    fun deleteParameters()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(parameters: Parameter)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(parameters:List<Parameter>?)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(parameters:Parameter)

    @Query("SELECT * FROM Parameter Where service_id =:id ORDER BY sort asc")
    fun getParametersLive(id:String):LiveData<List<Parameter>>

    @Query("SELECT * FROM Parameter Where service_id =:id ORDER BY sort asc")
    fun getParameters(id:String):List<Parameter>

    @Query("SELECT * FROM Image WHERE service_id = :id ")
    fun getImages(id:String):List<Image>

}