package com.example.sookwalk.data.local

import android.R.attr.version
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sookwalk.data.local.dao.FavoriteDao
import com.example.sookwalk.data.local.dao.GoalDao
import com.example.sookwalk.data.local.dao.NotificationDao
import com.example.sookwalk.data.local.dao.SearchHistoryDao
import com.example.sookwalk.data.local.dao.StepDao
import com.example.sookwalk.data.local.dao.UserDao
import com.example.sookwalk.data.local.entity.goal.GoalEntity
import com.example.sookwalk.data.local.entity.map.FavoriteCategoryEntity
import com.example.sookwalk.data.local.entity.map.SavedPlaceEntity
import com.example.sookwalk.data.local.entity.map.SearchHistoryEntity
import com.example.sookwalk.data.local.entity.notification.NotificationEntity
import com.example.sookwalk.data.local.entity.steps.DailyStepEntity
import com.example.sookwalk.data.local.entity.user.UserEntity

@Database(
    entities = [
        UserEntity::class,
        DailyStepEntity::class,
        NotificationEntity::class,
        GoalEntity::class,
        FavoriteCategoryEntity::class,
        SavedPlaceEntity::class,
        SearchHistoryEntity::class
    ],
    version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    /* Dao 접근 함수 */
    abstract fun userDao(): UserDao

//    abstract fun stepDao(): StepDao
    abstract fun notificationDao(): NotificationDao
    abstract fun goalDao(): GoalDao
    abstract fun stepDao(): StepDao
    abstract fun favoriteDao(): FavoriteDao

    abstract fun searchHistoryDao(): SearchHistoryDao

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
}