package com.okurchenko.ecocity.di

import android.content.Context
import android.content.SharedPreferences
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
import com.okurchenko.ecocity.ui.main.MainViewModel
import com.okurchenko.ecocity.ui.main.fragments.map.MapViewModel
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
        Room.databaseBuilder(androidContext(), StationDatabase::class.java, StationDatabase::class.java.name).build()
    }

    single { LocaleHelper(androidContext()).getLocale() }

    single { DataBaseManager(get(), get()) }
}

private val retrofitModules = module {
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.NONE })
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

private val apiModule = module {
    single { get<Retrofit>().create(StationApi::class.java) }
}

private val repoModule = module {
    single { StationRepositoryImpl(get(), get(), get()) }
}

private val viewModels = module {
    viewModel { MainViewModel() }
    viewModel { HistoryViewModel() }
    viewModel { DetailsViewModel() }
    viewModel { MapViewModel() }
}

val appModules = listOf(applicationModule, retrofitModules, apiModule, repoModule, viewModels)