package com.esh7enly.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.esh7enly.domain.entity.userservices.Category

@Dao
interface CategoryDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: List<Category>?)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(category: Category)

    @Query("SELECT * from Category ORDER BY sort asc")
    fun getCategories():List<Category>

    @Query("SELECT count(*) FROM Category")
    fun getCategoriesCount():LiveData<Int>

    @Query("DELETE FROM Category")
    fun deleteCategories()
}