package com.github.frankfuenmayor.flutterhelper.settings

class BuildRunnerBuildKnownAnnotations(
    private val settingsService: SettingsService = SettingsService.getInstance()
) {
    fun findAnnotation(annotationIdentifier: String) = settingsService
        .state
        .buildRunnerBuildRunnerAnnotations
        .find { knownAnnotation ->
            knownAnnotation.identifier == annotationIdentifier
        }
}