package android.epicurius.ui

import android.content.Context
import android.epicurius.Dependencies
import android.epicurius.services.EpicuriusService
import android.epicurius.storage.Session
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class EpicuriusActivity : ComponentActivity() {

    val dependencies by lazy { application as Dependencies }
    abstract val viewModel: EpicuriusViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    inline fun <reified T : ViewModel> getViewModel() =
        createViewModel {
            T::class.java
                .getConstructor(
                    EpicuriusService::class.java,
                    Session::class.java,
                    Context::class.java
                )
                .newInstance(
                    dependencies.service,
                    dependencies.session,
                    this
                )
        }

    inline fun <reified T : ViewModel> ComponentActivity.createViewModel(crossinline block: () -> T) =
        viewModels<T> {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T = block() as T
            }
        }
}