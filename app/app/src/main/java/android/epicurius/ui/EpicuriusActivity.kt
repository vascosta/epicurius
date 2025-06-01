package android.epicurius.ui

import android.os.Bundle
import androidx.activity.ComponentActivity

abstract class EpicuriusActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}