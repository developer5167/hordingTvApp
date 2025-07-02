package com.kapil.tvtest.hilt
import com.kapil.tvtest.data.ApiService
import com.kapil.tvtest.data.RepositoryImpl
import com.kapil.tvtest.domain.UseCase.UseCase
import com.kapil.tvtest.domain.UserRepository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModules {

  @Provides
  @Singleton
  fun provideLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor().apply {
      level = HttpLoggingInterceptor.Level.BODY
    }
  }
  @Provides
  @Singleton
  fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
    return OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
  }

  @Provides
  @Singleton
  fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder().baseUrl("http://192.168.15.224:4000/").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build()
  }

  @Provides
  @Singleton
  fun providesApiService(retrofit: Retrofit): ApiService {
    return retrofit.create(ApiService::class.java)
  }

  @Provides
  @Singleton
  fun providesUserRepository(apiService: ApiService): Repository {
    return RepositoryImpl(apiService)
  }
  @Provides
  @Singleton
  fun providesUseCase(repository: Repository): UseCase {
    return  UseCase(repository)
  }

}