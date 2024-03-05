package com.akimov.mobilebank.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            val isDarkTheme: State<Boolean?> = dataStore.data
                .map { it.isDarkMode }
                .collectAsState(initial = null)

            if (isDarkTheme.value != null) {
                MobileBankTheme(darkTheme = isDarkTheme.value!!) {
                    AccountsScreen()
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    val transition = rememberInfiniteTransition()
                    val progress = transition.animateFloat(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = InfiniteRepeatableSpec(animation = TweenSpec())
                    )
                    CircularProgressIndicator(
                        color = Color(0xFF0990cb),
                        progress = progress.value
                    )
                }
            }

        }
    }
}
