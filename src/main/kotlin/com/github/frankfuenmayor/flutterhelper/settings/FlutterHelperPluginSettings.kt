package com.github.frankfuenmayor.flutterhelper.settings

import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerKnownAnnotation
import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerKnownAnnotation.Companion.builtInAnnotations
import com.intellij.openapi.project.Project

data class FlutterHelperPluginSettings(private var annotations: List<BuildRunnerKnownAnnotation> = emptyList()) {
    val buildRunnerKnownAnnotations: List<BuildRunnerKnownAnnotation>
        get() = builtInAnnotations + annotations
}

val Project.flutterHelperPluginSettings
    get(): FlutterHelperPluginSettings =
        FlutterHelperPluginSettingsService.getInstance(this).state


