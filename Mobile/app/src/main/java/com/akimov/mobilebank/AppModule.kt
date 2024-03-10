package com.akimov.mobilebank

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.room.Room
import androidx.work.WorkManager
import com.akimov.mobilebank.data.database.RoomDb
import com.akimov.mobilebank.data.datastore.UserPreferencesSerializer
import com.akimov.mobilebank.data.network.CoreService
import com.akimov.mobilebank.data.network.UserService
import com.akimov.mobilebank.data.repository.AccountsRepository
import com.akimov.mobilebank.data.workers.ChangeBalanceWorker
import com.akimov.mobilebank.data.workers.CreateAccountWorker
import com.akimov.mobilebank.data.workers.RenameAccountWorker
import com.akimov.mobilebank.ui.AccountsViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.workmanager.dsl.worker
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
            .baseUrl("http://192.168.0.12:443/")
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    factory<CoreService> {
        get<Retrofit>().create(CoreService::class.java)
    }

    factory<UserService> { get<Retrofit>().create(UserService::class.java) }

    factory {
        AccountsRepository(
            api = get(),
            dao = get<RoomDb>().accountsDao(),
            dataStore = get(),
            workManager = get()
        )
    }

    worker<CreateAccountWorker> {
        CreateAccountWorker(
            context = androidContext(),
            params = get(),
            api = get(),
            dataStore = get()
        )
    }

    worker<RenameAccountWorker> {
        RenameAccountWorker(
            context = androidContext(),
            params = get(),
            api = get(),
            dataStore = get()
        )
    }

    worker {
        ChangeBalanceWorker(
            context = androidContext(),
            params = get(),
            api = get()
        )
    }

    single<WorkManager> { WorkManager.getInstance(androidContext()) }

    viewModel { AccountsViewModel(dataStore = get(), repository = get()) }
}