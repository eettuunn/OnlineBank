package com.akimov.mobilebank

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.akimov.mobilebank.bankAccounts.AccountsViewModel
import com.akimov.mobilebank.data.datastore.UserPreferencesSerializer
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val Context.dataStore: DataStore<UserSettings> by dataStore(
    fileName = "user.proto",
    serializer = UserPreferencesSerializer
)

val appModule = module {
    single<DataStore<UserSettings>> {
        androidContext().dataStore
    }

    viewModel { AccountsViewModel(dataStore = get()) }
}