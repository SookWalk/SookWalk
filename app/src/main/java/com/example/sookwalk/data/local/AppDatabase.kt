package com.example.sookwalk.data.local

import android.R.attr.version
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sookwalk.data.local.dao.FavoritePlaceDao
import com.example.sookwalk.data.local.entity.map.CategoryPlaceCrossRef
import com.example.sookwalk.data.local.entity.map.FavoriteCategoryEntity
import com.example.sookwalk.data.local.entity.map.FavoritePlaceEntity

@Database(
    entities = [
        FavoritePlaceEntity::class,
        FavoriteCategoryEntity::class, // (추가) 그룹
        CategoryPlaceCrossRef::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /* Dao 접근 함수 */

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this){
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sookWalkDB"
                ).build().also { INSTANCE = it }
            }
        }
    }

    abstract fun favoritePlaceDao(): FavoritePlaceDao
}