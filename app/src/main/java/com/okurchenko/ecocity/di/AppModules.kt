package com.okurchenko.ecocity.di

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.network.StationApi
import com.okurchenko.ecocity.repository.LocationListener
import com.okurchenko.ecocity.repository.StationRepositoryImpl
import com.okurchenko.ecocity.repository.db.DataBaseManager
import com.okurchenko.ecocity.repository.db.StationDatabase
import com.okurchenko.ecocity.ui.details.fragments.details.DetailsViewModel
import com.okurchenko.ecocity.ui.details.fragments.history.HistoryViewModel
import com.okurchenko.ecocity.ui.main.StationsViewModel
import com.okurchenko.ecocity.ui.main.fragments.map.MapLocationViewModel
import com.okurchenko.ecocity.utils.LocaleHelper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private val applicationModule = module {

    single { LocationListener(androidContext()) }

    single<SharedPreferences> {
        androidContext().getSharedPreferences(androidContext().getString(R.string.app_name), Context.MODE_PRIVATE)
    }

    single {
        val dbClass = StationDatabase::class.java
        Room.databaseBuilder(androidContext(), dbClass, dbClass.name).build()
    }

    single { LocaleHelper(androidContext()).getLocale() }

    single { DataBaseManager(get(), get()) }
}

private val retrofitModules = module {
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    single<Retrofit> {
        Retrofit.Builder()
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().setLenient().create()))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl("https://eco-city.org.ua/")
            .build()
    }
}

private val apiModule = module { single { get<Retrofit>().create(StationApi::class.java) } }

private val repoModule = module { single { StationRepositoryImpl(get(), get(), get()) } }

private val viewModels = module {
    viewModel { StationsViewModel() }
    viewModel { (handle: SavedStateHandle) -> HistoryViewModel(handle) }
    viewModel { DetailsViewModel() }
    viewModel { MapLocationViewModel() }
}

val appModules = listOf(applicationModule, retrofitModules, apiModule, repoModule, viewModels)
