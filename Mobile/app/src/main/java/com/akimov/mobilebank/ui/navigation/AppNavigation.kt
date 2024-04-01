package com.akimov.mobilebank.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.akimov.mobilebank.UserSettings
import com.akimov.mobilebank.ui.accounts.AccountsScreen
import com.akimov.mobilebank.ui.loan.GetLoanScreen
import com.akimov.mobilebank.ui.login.LoginScreen
import com.akimov.mobilebank.ui.operations.OperationsScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.inject

private const val LOAN_DESTINATION = "loan"
private const val LOGIN_DESTINATION = "login"
private const val OPERATIONS_DESTINATION = "operations"
private const val ACCOUNTS_DESTINATION = "accounts"

const val ACCOUNT_ID_KEY = "ACCOUNT_ID"

suspend fun getStartDestination(): String {
    val dataStore by inject<DataStore<UserSettings>>(DataStore::class.java)

    val isUserLogin: Boolean = dataStore.data.map {
        Log.e("MainActivity", "isUserLogin: ${it.uuid}, ${it.selectedScreen}")
        it.uuid != "" && it.uuid != null
    }.first()

    return if (isUserLogin) ACCOUNTS_DESTINATION else LOGIN_DESTINATION
}

@Composable
fun AppNavigation() {
    val startDestination = runBlocking {
        getStartDestination()
    }

    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = LOAN_DESTINATION) {
            GetLoanScreen(
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(LOGIN_DESTINATION) {
            LoginScreen(
                navigateToNextScreen = {
                    navController.popBackStack(ACCOUNTS_DESTINATION, false)
                }
            )
        }
        composable(route = "$OPERATIONS_DESTINATION/{$ACCOUNT_ID_KEY}") {
            OperationsScreen(
                accountID = it.arguments?.getString(
                    ACCOUNT_ID_KEY
                )
                    ?: throw NullPointerException("ID счёта не был передан в аргументы"),
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = ACCOUNTS_DESTINATION
        ) {
            AccountsScreen(
                navigateToOperations = { accountId ->
                    navController.navigate(
                        route = "$OPERATIONS_DESTINATION/$accountId",
                    ) {
                        restoreState = true
                        launchSingleTop = true
                    }

                },
                navigateToGetLoan = {
                    navController.navigate(LOAN_DESTINATION) {
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
