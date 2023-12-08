package com.esh7enly.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.esh7enly.domain.entity.userservices.Image

@Dao
interface ImageDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(image: Image)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(images:List<Image>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(images:Image)

    @Query("SELECT * From Image WHERE service_id=:id")
    fun getImages(id:String):LiveData<List<Image>>

    @Query("SELECT * From Image")
    fun getImages():LiveData<List<Image>>

    @Query("SELECT count(*) FROM Image")
    fun getImagesCount():Int

    @Query("DELETE From Image")
    fun deleteImages()
}