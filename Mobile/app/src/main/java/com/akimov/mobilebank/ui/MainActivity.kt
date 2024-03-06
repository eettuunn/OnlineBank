package com.akimov.mobilebank.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.datastore.core.DataStore
import com.akimov.mobilebank.UserSettings
import com.akimov.mobilebank.bankAccounts.AccountsScreen
import com.akimov.mobilebank.ui.theme.MobileBankTheme
import kotlinx.coroutines.flow.map
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val dataStore by inject<DataStore<UserSettings>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkTheme: State<Boolean?> =
                dataStore.data.map { it.isDarkMode }.collectAsState(initial = null)

            MobileBankTheme(darkTheme = isDarkTheme.value ?: isSystemInDarkTheme()) {
                AccountsScreen()
            }
        }
    }
}