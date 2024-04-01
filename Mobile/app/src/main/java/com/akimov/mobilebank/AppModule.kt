package com.akimov.mobilebank

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.room.Room
import androidx.work.WorkManager
import com.akimov.mobilebank.data.database.RoomDb
import com.akimov.mobilebank.data.datastore.UserPreferencesSerializer
import com.akimov.mobilebank.data.network.CoreService
import com.akimov.mobilebank.data.network.LoanService
import com.akimov.mobilebank.data.network.UserService
import com.akimov.mobilebank.data.repository.Repository
import com.akimov.mobilebank.data.workers.ChangeBalanceWorker
import com.akimov.mobilebank.data.workers.CloseAccountWorker
import com.akimov.mobilebank.data.workers.CreateAccountWorker
import com.akimov.mobilebank.data.workers.GetLoanWorker
import com.akimov.mobilebank.data.workers.RenameAccountWorker
import com.akimov.mobilebank.domain.GetSortedByDateOperationsUseCase
import com.akimov.mobilebank.ui.accounts.AccountsViewModel
import com.akimov.mobilebank.ui.loan.LoanViewModel
import com.akimov.mobilebank.ui.operations.OperationsViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.qualifier.named
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
            .connectTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    // Gateway
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("http://192.168.0.12:3000/")
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val hostIp = "http://92.118.114.182"
    single<Retrofit>(named("CORE")) {
        Retrofit.Builder()
            .baseUrl("$hostIp:8080/")
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<Retrofit>(named("USERS")) {
        Retrofit.Builder()
            .baseUrl("$hostIp:7788/")
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<Retrofit>(named("LOANS")) {
        Retrofit.Builder()
            .baseUrl("$hostIp:8877/")
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    factory<CoreService> {
        get<Retrofit>(named("CORE")).create(CoreService::class.java)
    }

    factory<UserService> { get<Retrofit>(named("USERS")).create(UserService::class.java) }

    factory<LoanService> { get<Retrofit>(named("LOANS")).create(LoanService::class.java) }

    single {
        Repository(
            coreService = get(),
            accountsDao = get<RoomDb>().accountsDao(),
            dataStore = get(),
            workManager = get(),
            creditsDao = get<RoomDb>().creditsDao(),
            operationsDao = get<RoomDb>().operationsDao(),
            loanService = get()
        )
    }

    factory {
        GetSortedByDateOperationsUseCase(
            repository = get()
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

    worker<GetLoanWorker> {
        GetLoanWorker(
            context = androidContext(),
            params = get(),
            loanService = get(),
            dataStore = get()
        )
    }

    worker {
        ChangeBalanceWorker(
            context = androidContext(),
            params = get(),
            api = get<CoreService>(),
            dataStore = get()
        )
    }

    worker {
        CloseAccountWorker(
            context = androidContext(),
            params = get(),
            dataStore = get(),
            coreService = get()
        )
    }

    single<WorkManager> { WorkManager.getInstance(androidContext()) }

    viewModel { AccountsViewModel(dataStore = get(), repository = get()) }
    viewModel {
        LoanViewModel(
            creditsDao = get<RoomDb>().creditsDao(),
            accountsDao = get<RoomDb>().accountsDao(),
            repository = get(),
        )
    }
    viewModel {
        OperationsViewModel(
            stavedStateHandle = get(),
            repository = get()
        )
    }
}