package com.github.frankfuenmayor.flutterhelper.codeInsight

import com.github.frankfuenmayor.flutterhelper.settings.SettingsService

class BuildRunnerBuildKnownAnnotations(
    private val settingsService: SettingsService = SettingsService.getInstance()
) {
    fun findAnnotation(annotationIdentifier: String) = settingsService
        .state
        .buildRunnerAnnotations
        .find { knownAnnotation ->
            knownAnnotation.identifier == annotationIdentifier
        }
}