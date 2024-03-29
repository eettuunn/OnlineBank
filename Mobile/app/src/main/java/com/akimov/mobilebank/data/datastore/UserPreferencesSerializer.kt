package com.akimov.mobilebank.data.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.akimov.mobilebank.UserSettings
import com.akimov.mobilebank.data.models.SelectedScreen
import java.io.InputStream
import java.io.OutputStream

object UserPreferencesSerializer : Serializer<UserSettings> {
    override val defaultValue: UserSettings = UserSettings
        .getDefaultInstance()
        .toBuilder()
        .setUuid("")
        .setSelectedScreen(SelectedScreen.ACCOUNTS.name)
        .setName("Максим")
        .setIsDarkMode(false)
        .build()

    override suspend fun readFrom(input: InputStream): UserSettings {
        try {
            return UserSettings.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: UserSettings,
        output: OutputStream,
    ) = t.writeTo(output)
}