package com.github.frankfuenmayor.flutterhelper.settings

import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerAnnotation
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.SerializablePersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "FlutterHelperPluginSettings",
    storages = [Storage("FlutterHelperPluginSettings.xml")]
)
@Service(Service.Level.APP)
class SettingsService :
    SerializablePersistentStateComponent<Settings>(Settings()) {

    var annotations: List<BuildRunnerAnnotation>
        get() = state.annotations
        set(value) {
            updateState {
                state.copy(value)
            }

        }

    companion object {
        @JvmStatic
        fun getInstance(): SettingsService =
            ApplicationManager.getApplication().getService(SettingsService::class.java)
    }
}