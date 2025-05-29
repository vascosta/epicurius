package android.epicurius

import android.epicurius.services.EpicuriusService
import android.epicurius.storage.Session

interface Dependencies {
    val service: EpicuriusService
    val session: Session
}