package com.akimov.mobilebank

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.room.Room
import com.akimov.mobilebank.bankAccounts.AccountsViewModel
import com.akimov.mobilebank.data.database.RoomDb
import com.akimov.mobilebank.data.datastore.UserPreferencesSerializer
import com.akimov.mobilebank.data.network.CoreService
import com.akimov.mobilebank.data.repository.AccountsRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private val Context.dataStore: DataStore<UserSettings> by dataStore(
    fileName = "user.proto",
    serializer = UserPreferencesSerializer
)

val appModule = module {
    single<RoomDb> {
        Room.databaseBuilder(
            context = androidContext(),
            klass = RoomDb::class.java,
            name = "room_db"
        ).build()
    }

    single<DataStore<UserSettings>> {
        androidContext().dataStore
    }

    single<OkHttpClient> {
        OkHttpClient()
            .newBuilder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("http://192.168.0.12:8080/")
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    factory<CoreService> {
        get<Retrofit>().create(CoreService::class.java)
    }

    factory {
        AccountsRepository(
            api = get(),
            dao = get<RoomDb>().accountsDao(),
            dataStore = get()
        )
    }

    viewModel { AccountsViewModel(dataStore = get(), repository = get()) }
}