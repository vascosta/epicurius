package android.epicurius

import android.app.Application
import android.content.Context
import android.epicurius.services.EpicuriusService
import android.epicurius.services.http.HttpService
import android.epicurius.storage.SessionDataStore
import android.epicurius.ui.notifications.fridge.scheduleDailyProductCheck
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import okhttp3.OkHttpClient
import java.lang.reflect.Type
import java.time.LocalDate


class EpicuriusApplication : Application(), Dependencies {

    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, JsonDeserializer { json, _, _ ->
            LocalDate.parse(json.asString)
        })
        .registerTypeAdapter(LocalDate::class.java, JsonSerializer { src: LocalDate, _: Type, _: JsonSerializationContext ->
            JsonPrimitive(src.toString())
        })
        .create()

    private val httpService = HttpService(BASE_URL, OkHttpClient(), gson)

    override val service = EpicuriusService(httpService)
    override val session by lazy {
        SessionDataStore(applicationContext.sessionDataStore, gson)
    }

    override fun onCreate() {
        super.onCreate()
        scheduleDailyProductCheck(this)
    }

    companion object {
        private const val BASE_URL = "http://10.0.2.2:8080/api"
        // private const val BASE_URL = "http://35.205.65.208:8080/api" GCP VM
        //private const val BASE_URL = "http://192.168.1.30:8080/api"
    }
}

val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
