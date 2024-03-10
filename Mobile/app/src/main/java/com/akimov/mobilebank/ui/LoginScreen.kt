package com.akimov.mobilebank.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import com.akimov.mobilebank.R
import com.akimov.mobilebank.UserSettings
import com.akimov.mobilebank.data.models.IdModel
import com.akimov.mobilebank.data.models.SendingEmail
import com.akimov.mobilebank.data.network.UserService
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun LoginScreen() {
    val api = koinInject<UserService>()
    val dataStore = koinInject<DataStore<UserSettings>>()
    val scope = rememberCoroutineScope()

    Content {
        scope.launch {
            try {
                val id: IdModel = api.login(SendingEmail(it))
                dataStore.updateData {
                    it.toBuilder().setUuid(id.id.toString()).build()
                }
            } catch (e: Exception) {
                Log.e("LoginScreen", "Error: ${e.message}")
            }
        }
    }
}

@Composable
private fun Content(onClick: (String) -> Unit) {
    Box(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        var text by remember {
            mutableStateOf("")
        }
        val focusRequester = remember { FocusRequester() }
        TextField(modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .focusRequester(focusRequester), value = text, onValueChange = {
            text = it
        }, shape = RoundedCornerShape(16.dp), colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.tertiary,
            unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color(0xFF0990cb),
        ), trailingIcon = {
            if (text.isNotEmpty()) {
                Icon(
                    modifier = Modifier.clickable {
                        text = ""
                    },
                    imageVector = Icons.Filled.Clear,
                    contentDescription = null,
                )
            }
        }, placeholder = {
            Text(text = stringResource(id = R.string.email))
        }, singleLine = true, keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
        ), keyboardActions = KeyboardActions(onDone = {
            onClick(text)
        })
        )
    }
}