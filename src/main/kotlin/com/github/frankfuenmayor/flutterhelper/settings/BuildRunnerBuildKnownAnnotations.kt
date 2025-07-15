package com.github.frankfuenmayor.flutterhelper.settings

class BuildRunnerBuildKnownAnnotations(
    settingsService: SettingsService = SettingsService.getInstance()
) {

    val settings = settingsService.state

    fun isKnown(annotationIdentifier: String) = settings
        .annotations
        .any { knownAnnotation ->
            knownAnnotation.identifier == annotationIdentifier
        }

    fun findAnnotation(annotationIdentifier: String) = settings
        .annotations
        .find { knownAnnotation ->
            knownAnnotation.identifier == annotationIdentifier
        }
}