package com.github.frankfuenmayor.flutterhelper.codeInsight

import com.github.frankfuenmayor.flutterhelper.settings.SettingsService
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.jetbrains.lang.dart.DartFileType
import com.jetbrains.lang.dart.DartTokenTypes

class RunBuilderRunnerLineMarkerProvider(
    private val navigationHandler: GutterIconNavigationHandler<PsiElement> = RunBuilderRunnerNavigationHandler(),
    private val knownAnnotations: BuildRunnerBuildKnownAnnotations = BuildRunnerBuildKnownAnnotations()
) : LineMarkerProvider {
    override fun getLineMarkerInfo(psiElement: PsiElement): LineMarkerInfo<*>? {

        val isDartFile =
            psiElement.containingFile.virtualFile.fileType == DartFileType.INSTANCE;

        if (!isDartFile) {
            return null
        }

        if (psiElement.elementType != DartTokenTypes.AT) {
            return null
        }

        val annotationIdentifier = psiElement.nextSibling.text

        val annotation = knownAnnotations.findAnnotation(annotationIdentifier) ?: return null

        return BuildRunnerLineMarkerInfo(psiElement, navigationHandler)
    }
}

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