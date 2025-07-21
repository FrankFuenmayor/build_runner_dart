package com.github.frankfuenmayor.flutterhelper.buildrunner.codeInsight

import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerAnnotation
import com.github.frankfuenmayor.flutterhelper.buildrunner.settings.BuildRunnerBuildKnownAnnotations
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.jetbrains.lang.dart.DartFileType
import com.jetbrains.lang.dart.DartTokenTypes


typealias GutterIconNavigationHandlerProvider = (annotation: BuildRunnerAnnotation) -> GutterIconNavigationHandler<PsiElement>

class RunBuilderRunnerLineMarkerProvider(
    private val createNavigationHandler: GutterIconNavigationHandlerProvider = { RunBuilderRunnerNavigationHandler(it) },
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

        val buildRunnerAnnotation =
            knownAnnotations.findAnnotation(annotationIdentifier) ?: return null

        return BuildRunnerLineMarkerInfo(
            psiElement = psiElement,
            navigationHandler = createNavigationHandler(buildRunnerAnnotation),
        )
    }
}
