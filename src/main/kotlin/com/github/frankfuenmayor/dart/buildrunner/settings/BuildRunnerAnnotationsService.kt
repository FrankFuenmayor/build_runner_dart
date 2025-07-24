package com.github.frankfuenmayor.dart.buildrunner.settings

import com.github.frankfuenmayor.dart.buildrunner.BuildRunnerAnnotation
import com.github.frankfuenmayor.dart.buildrunner.BuildRunnerAnnotation.Companion.builtIns
import com.github.frankfuenmayor.dart.buildrunner.BuildRunnerAnnotations
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
class BuildRunnerAnnotationsService :
    SerializablePersistentStateComponent<BuildRunnerAnnotationsServiceState>(BuildRunnerAnnotationsServiceState()),
    BuildRunnerAnnotations {

    override fun getAnnotations(): List<BuildRunnerAnnotation> {
        return state.annotations
    }

    override fun setAnnotations(annotations: List<BuildRunnerAnnotation>) {
        updateState {
            state.copy(annotations)
        }
    }

    companion object {
        @JvmStatic
        fun getInstance(): BuildRunnerAnnotationsService =
            ApplicationManager.getApplication().getService(BuildRunnerAnnotationsService::class.java)
    }
}

data class BuildRunnerAnnotationsServiceState(
    @JvmField val annotations: List<BuildRunnerAnnotation> = builtIns
)
