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

        val unknownAnnotation =
            psiElement.project.flutterHelperPluginSettings.buildRunnerKnownAnnotations.none { knownAnnotation ->
                knownAnnotation.identifier.removePrefix("@") == annotationIdentifier.removePrefix("@")
            }

        if (unknownAnnotation) {
            return null
        }

        return BuildRunnerLineMarkerInfo(psiElement, navigationHandler)
    }
}

