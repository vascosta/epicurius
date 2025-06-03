package android.epicurius

import android.app.Application
import android.content.Context
import android.epicurius.services.EpicuriusService
import android.epicurius.services.http.HttpService
import android.epicurius.storage.SessionDataStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import okhttp3.OkHttpClient


class EpicuriusApplication : Application(), Dependencies {
    private val gson = Gson()
    private val httpService = HttpService(BASE_URL, OkHttpClient(), gson)

    override val service = EpicuriusService(httpService)
    override val session by lazy {
        SessionDataStore(applicationContext.sessionDataStore, gson)
    }

    companion object {
        private const val BASE_URL = "http://10.0.2.2:8080/api"
    }
}

val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

