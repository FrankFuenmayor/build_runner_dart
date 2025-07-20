package com.github.frankfuenmayor.flutterhelper.buildrunner.settings

class BuildRunnerBuildKnownAnnotations(
    settingsService: SettingsService = SettingsService.getInstance()
) {

    val annotations get() = SettingsService.getInstance().annotations

    fun isKnown(annotationIdentifier: String) = annotations
        .any { knownAnnotation ->
            knownAnnotation.identifier == annotationIdentifier
        }

    fun findAnnotation(annotationIdentifier: String) = annotations
        .find { knownAnnotation ->
            knownAnnotation.identifier == annotationIdentifier
        }
}