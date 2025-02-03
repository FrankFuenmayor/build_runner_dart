package com.github.frankfuenmayor.flutterhelper.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "FlutterHelperPluginSettings",
    storages = [Storage("FlutterHelperPluginSettings.xml")]
)
@Service(Service.Level.APP)
class SettingsService :
    PersistentStateComponent<Settings> {
    private var settings = Settings()

    override fun getState(): Settings = settings

    override fun loadState(state: Settings) {
        settings = state
    }

    companion object {
        fun getInstance(): SettingsService =
            ApplicationManager.getApplication().getService(SettingsService::class.java)
    }
}