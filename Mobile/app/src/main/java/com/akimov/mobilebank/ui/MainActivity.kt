package com.akimov.mobilebank.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import com.akimov.mobilebank.UserSettings
import com.akimov.mobilebank.data.models.SelectedScreen
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
                val isUserLogin by dataStore.data.map {
                    Log.e("MainActivity", "isUserLogin: ${it.uuid}, ${it.selectedScreen}")
                    it.uuid != "" && it.uuid != null
                }
                    .collectAsState(initial = null)
                when (isUserLogin) {
                    true -> {
                        val selectedScreen by dataStore.data.map { it.selectedScreen }
                            .collectAsState(initial = null)
                        when (selectedScreen) {
                            SelectedScreen.ACCOUNTS.name -> AccountsScreen()
                            SelectedScreen.ADD_CREDIT.name -> GetLoanContent(Modifier.fillMaxSize())
                            SelectedScreen.OPERATIONS.name -> OperationsScreen()
                            else -> AccountsScreen()
                        }
                    }

                    false -> LoginScreen()
                    null -> Unit
                }
            }
        }
    }
}