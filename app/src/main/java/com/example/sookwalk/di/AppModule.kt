package com.example.sookwalk.di

import android.content.Context
import androidx.room.Room
import com.example.sookwalk.data.local.AppDatabase
import com.example.sookwalk.data.local.dao.FavoriteDao
import com.example.sookwalk.data.local.dao.GoalDao
import com.example.sookwalk.data.local.dao.NotificationDao
import com.example.sookwalk.data.local.dao.SearchHistoryDao
import com.example.sookwalk.data.local.dao.StepDao
import com.example.sookwalk.data.local.dao.UserDao
import com.example.sookwalk.data.repository.GoalRepository
import com.example.sookwalk.data.repository.MapRepository
import com.example.sookwalk.data.repository.NotificationRepository
import com.example.sookwalk.data.repository.UserRepository
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.Firebase
import com.google.firebase.app
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import kotlin.jvm.java


@Module // 이 클래스가 Hilt에 의존성을 제공함을 알림
@InstallIn(SingletonComponent::class) // 앱 전역에서 사용할 수 있게 함
object AppModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore =
        FirebaseFirestore.getInstance(Firebase.app, "sookwalk")

    // Room DB 인스턴스 제공
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        // Room DB 생성
        return Room.databaseBuilder(
            context, // 현재 앱의 실행 환경
            AppDatabase::class.java, // DB 클래스 타입 정보
            "sookWalkDB" // DB 이름
        ).build() // DB 생성
    }


    // 객체 간 연결 규칙을 명시한다
    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    // MyPage
    @Provides
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepository(userDao)
    }

    // Notification
    @Provides
    fun provideNotificationDao(appDatabase: AppDatabase): NotificationDao {
        return appDatabase.notificationDao()
    }

    @Provides
    fun provideNotificationRepository(notificationDao: NotificationDao): NotificationRepository {
        return NotificationRepository(notificationDao)
    }

    // Goal
    @Provides
    fun provideGoalDao(appDatabase: AppDatabase): GoalDao {
        return appDatabase.goalDao()
    }

    @Provides
    fun provideGoalRepository(goalDao: GoalDao): GoalRepository {
        return GoalRepository(goalDao)
    }

    // Step
    @Provides
    fun provideStepDao(appDatabase: AppDatabase): StepDao {
        return appDatabase.stepDao()
    }

    // --- Map & Places 관련 추가 ---
    @Provides
    fun provideFavoriteDao(appDatabase: AppDatabase): FavoriteDao {
        return appDatabase.favoriteDao()
    }

    @Provides
    @Singleton
    fun providePlacesClient(@ApplicationContext context: Context): PlacesClient {
        return Places.createClient(context)
    }

    @Provides
    @Singleton
    fun provideMapRepository(
        favoriteDao: FavoriteDao,
        searchHistoryDao: SearchHistoryDao,
        placesClient: PlacesClient
    ): MapRepository {
        return MapRepository(favoriteDao, searchHistoryDao, placesClient)
    }

    @Provides
    fun provideSearchHistoryDao(appDatabase: AppDatabase): SearchHistoryDao {
        return appDatabase.searchHistoryDao()
    }
}