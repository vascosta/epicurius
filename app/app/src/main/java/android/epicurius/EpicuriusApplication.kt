package android.epicurius

import android.app.Application
import android.content.Context
import android.epicurius.services.EpicuriusService
import android.epicurius.services.http.HttpService
import android.epicurius.storage.SessionDataStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import okhttp3.OkHttpClient
import java.time.LocalDate
import java.util.Base64
import java.util.concurrent.TimeUnit


class EpicuriusApplication : Application(), Dependencies {

    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, JsonDeserializer { json, _, _ ->
            LocalDate.parse(json.asString)
        })
        .create()

    private val httpService = HttpService(
        BASE_URL,
        OkHttpClient.Builder()
            .callTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build(),
        gson
    )

    override val service = EpicuriusService(httpService)
    override val session by lazy {
        SessionDataStore(applicationContext.sessionDataStore, gson)
    }

    companion object {
        private const val BASE_URL = "http://10.0.2.2:8080/api"

        val byteArrayListDeserializer = JsonDeserializer { json, _, _ ->
            json.asJsonArray.map { Base64.getDecoder().decode(it.asString) } as List<ByteArray>
        }
    }
}

val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
