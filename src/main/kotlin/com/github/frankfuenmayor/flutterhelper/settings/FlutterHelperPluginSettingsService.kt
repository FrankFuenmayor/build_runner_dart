package com.github.frankfuenmayor.flutterhelper.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

@State(
    name = "FlutterHelperPluginSettings",
    storages = [Storage("FlutterHelperPluginSettings.xml")]
)
@Service(Service.Level.PROJECT)
class FlutterHelperPluginSettingsService :
    PersistentStateComponent<FlutterHelperPluginSettings> {
    private var settings = FlutterHelperPluginSettings()

    override fun getState(): FlutterHelperPluginSettings = settings

    override fun loadState(state: FlutterHelperPluginSettings) {
        settings = state
    }

    companion object {
        fun getInstance(project: Project): FlutterHelperPluginSettingsService =
            project.getService(FlutterHelperPluginSettingsService::class.java)
    }
}