package com.gta.data.source

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

class MessageTokenDataSource @Inject constructor(@ApplicationContext private val context: Context) {
    private val tokenKey = stringPreferencesKey(name = "token")

    fun setMessageToken(token: String): Flow<Boolean> = flow {
        kotlin.runCatching {
            context.datastore.edit { preferences ->
                preferences[tokenKey] = token
            }
        }.isSuccess
    }

    fun getMessageToken(): Flow<String> {
        return context.datastore.data.map { preferences ->
            preferences[tokenKey] ?: ""
        }
    }
}
