package com.esh7enly.data.local

import androidx.room.Database
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.esh7enly.domain.entity.Converters
import com.esh7enly.domain.entity.FawryEntity
import com.esh7enly.domain.entity.VersionEntity
import com.esh7enly.domain.entity.userservices.*

@Database(
    entities = [Category::class,Provider::class,
        Service::class, Parameter::class,Image::class, VersionEntity::class, FawryEntity::class],
    version = 9 ,                // <- Database version
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class DatabaseRoom: androidx.room.RoomDatabase()
{
    abstract fun categoryDao(): CategoryDao
    abstract fun providerDao(): ProviderDao
    abstract fun serviceDao(): ServiceDao
    abstract fun parameterDao(): ParameterDao
    abstract fun imageDao(): ImageDao
    abstract fun userDao(): UserDao
    abstract fun fawryDao(): FawryDao

    val MIGRATION1_2 = object : Migration(1,2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("")
        }

    }
}