package com.github.frankfuenmayor.flutterhelper.buildrunner.settings

import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerAnnotation
import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerAnnotation.Companion.builtIns
import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerAnnotations
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
class BuildRunnerAnnotationsService : SerializablePersistentStateComponent<List<BuildRunnerAnnotation>>(builtIns),
    BuildRunnerAnnotations {

    override fun getAnnotations(): List<BuildRunnerAnnotation> {
        return state
    }

    override fun setAnnotations(annotation: List<BuildRunnerAnnotation>) {
        updateState {
            annotation
        }
    }

    companion object {
        @JvmStatic
        fun getInstance(): BuildRunnerAnnotationsService =
            ApplicationManager.getApplication().getService(BuildRunnerAnnotationsService::class.java)
    }
}