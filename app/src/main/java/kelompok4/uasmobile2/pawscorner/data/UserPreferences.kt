package kelompok4.uasmobile2.pawscorner.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Buat DataStore sebagai extension property di Context
val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_PHONE = stringPreferencesKey("user_phone")
        private val USER_PASSWORD = stringPreferencesKey("user_password")
    }

    // Flow untuk membaca status login
    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_LOGGED_IN] ?: false
        }

    val userName: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[USER_NAME] ?: ""
        }

    val userEmail: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[USER_EMAIL] ?: ""
        }

    val userPhone: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[USER_PHONE] ?: ""
        }

    val userPassword: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[USER_PASSWORD] ?: ""
        }

    // Fungsi untuk menyimpan status login
    suspend fun setLoggedIn(loggedIn: Boolean) {
        context.dataStore.edit { it[IS_LOGGED_IN] = loggedIn }
    }

    suspend fun setUserData(name: String, email: String, password: String, phone: String) {
        context.dataStore.edit {
            it[USER_NAME] = name
            it[USER_EMAIL] = email
            it[USER_PASSWORD] = password
            it[USER_PHONE] = phone
        }
    }

    suspend fun clearUserData() {
        context.dataStore.edit {
            it.clear()
        }
    }
}
