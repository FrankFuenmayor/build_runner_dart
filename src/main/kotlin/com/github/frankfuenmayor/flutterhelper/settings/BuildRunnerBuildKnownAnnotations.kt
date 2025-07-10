package com.github.frankfuenmayor.flutterhelper.settings

class BuildRunnerBuildKnownAnnotations(
    settingsService: SettingsService = SettingsService.getInstance()
) {

    val settings = settingsService.state

    fun isKnown(annotationIdentifier: String) = settings
        .buildRunnerBuildRunnerAnnotations
        .any { knownAnnotation ->
            knownAnnotation.identifier == annotationIdentifier
        }

    fun findAnnotation(annotationIdentifier: String) = settings
        .buildRunnerBuildRunnerAnnotations
        .find { knownAnnotation ->
            knownAnnotation.identifier == annotationIdentifier
        }
}