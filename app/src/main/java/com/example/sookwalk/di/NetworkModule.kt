package com.example.sookwalk.di

import com.example.sookwalk.data.remote.ApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    // 컨버터로 Moshi 사용
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Provides
    @Singleton
    // 로깅 인터셉터 추가 (네트워크 요청/응답 확인용)
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides
    @Singleton
    // 레트로핏 인스턴스 생성
    fun provideRetrofit(moshi: Moshi, client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()


    @Provides
    @Singleton
    // API 인터페이스 객체 생성
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)


    /*

    // 리포지토리 추가
    // 아래는 강의 자료에 있던 예시 리포지토리 코드
    @Provides
    @Singleton
    fun providePostRepository(api: ApiService): PostRepository =
        PostRepository(api)

    /* 그 외에 리포지토리 계속 추가 필요 */


     */
}