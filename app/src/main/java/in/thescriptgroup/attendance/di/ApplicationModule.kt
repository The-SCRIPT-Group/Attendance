package `in`.thescriptgroup.attendance.di

import `in`.thescriptgroup.attendance.api.AttendanceService
import `in`.thescriptgroup.attendance.api.AttendanceService.ApiClient.BASE_URL
import `in`.thescriptgroup.attendance.models.Subject
import `in`.thescriptgroup.attendance.utils.Constants
import android.content.Context
import android.content.SharedPreferences
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    fun provideSharedPrefs(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(Constants.preference_file_key, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideRetrofit(): AttendanceService =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                MoshiConverterFactory.create(providesMoshi())
            )
            .build()
            .create(AttendanceService::class.java)

    @Singleton
    @Provides
    fun providesMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Singleton
    @Provides
    fun providesShippedCheatAdapter(): JsonAdapter<List<Subject>> {
        val subjectListType =
            Types.newParameterizedType(List::class.java, Subject::class.java)
        return providesMoshi().adapter(subjectListType)
    }
}