package com.github.frankfuenmayor.flutterhelper.buildrunner

import com.github.frankfuenmayor.flutterhelper.settings.flutterHelperPluginSettings
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.jetbrains.lang.dart.DartFileType
import com.jetbrains.lang.dart.DartTokenTypes.AT

class RunBuilderRunnerLineMarkerProvider(
    private val navigationHandler: GutterIconNavigationHandler<PsiElement> = RunBuilderRunnerNavigationHandler()
) : LineMarkerProvider {
    override fun getLineMarkerInfo(psiElement: PsiElement): LineMarkerInfo<*>? {

        val isDartFile =
            psiElement.containingFile.virtualFile.fileType == DartFileType.INSTANCE;

        if (!isDartFile) {
            return null
        }

        if (psiElement.elementType != AT) {
            return null
        }

        val annotationIdentifier = psiElement.nextSibling.text

        val annotation =
            psiElement.project.flutterHelperPluginSettings.buildRunnerKnownAnnotations.find { knownAnnotation ->
                knownAnnotation.identifier == annotationIdentifier
            }

        if (annotation == null) {
            return null
        }

        return BuildRunnerLineMarkerInfo(psiElement, navigationHandler)
    }
}

