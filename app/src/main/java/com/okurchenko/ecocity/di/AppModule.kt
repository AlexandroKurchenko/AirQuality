package com.okurchenko.ecocity.di

import android.preference.PreferenceManager
import androidx.room.Room
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.okurchenko.ecocity.network.StationApi
import com.okurchenko.ecocity.repository.StationRepositoryImpl
import com.okurchenko.ecocity.repository.db.DataBaseManager
import com.okurchenko.ecocity.repository.db.StationDatabase
import com.okurchenko.ecocity.ui.details.fragments.details.DetailsViewModel
import com.okurchenko.ecocity.ui.details.fragments.history.HistoryViewModel
import com.okurchenko.ecocity.ui.main.MainViewModel
import com.okurchenko.ecocity.utils.LocaleHelper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val mainModule = module {

    val stationDatabase = named("StationDatabase")
    val locationHelper = named("LocationHelper")
    val databaseManager = named("DatabaseManager")
    val prefs = named("PreferenceManager")
    val api = named("StationApi")
    val stationRepository = named("StationRepository")

    single(api) {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://eco-city.org.ua/"/*BuildConfig.SERVER_URL*/)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().setLenient().create()))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .callFactory(okHttpClient)
            .build()
        retrofit.create(StationApi::class.java)
    }

    single(stationDatabase) {
        Room.databaseBuilder(androidContext(), StationDatabase::class.java, "station")
//            .addMigrations(Migrations.MIGRATION_1_2)
//            .addMigrations(Migrations.MIGRATION_2_3)
//            .addMigrations(Migrations.MIGRATION_3_4)
            .build()
    }

    single(locationHelper) { LocaleHelper(androidContext()).getLocale() }

    single(databaseManager) { DataBaseManager(get(stationDatabase), get(locationHelper)) }

    single(prefs) { PreferenceManager.getDefaultSharedPreferences(androidContext()) }

    single(stationRepository) { StationRepositoryImpl(get(api), get(databaseManager), get(prefs)) }

    viewModel { MainViewModel(get(stationRepository)) }

    viewModel { HistoryViewModel(get(stationRepository)) }

    viewModel { DetailsViewModel(get(stationRepository)) }

}