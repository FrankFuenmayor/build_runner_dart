package com.github.frankfuenmayor.dart.buildrunner.codeInsight

import com.github.frankfuenmayor.dart.buildrunner.BuildRunnerAnnotations
import com.github.frankfuenmayor.dart.buildrunner.BuildRunnerData
import com.github.frankfuenmayor.dart.buildrunner.BuildRunnerDataBuilder
import com.github.frankfuenmayor.dart.buildrunner.components.BuildRunnerAnnotationsService
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.jetbrains.lang.dart.DartFileType
import com.jetbrains.lang.dart.DartTokenTypes


typealias GutterIconNavigationHandlerProvider = (buildRunnerData: BuildRunnerData) -> GutterIconNavigationHandler<PsiElement>

class RunBuilderRunnerLineMarkerProvider(
    private val createNavigationHandler: GutterIconNavigationHandlerProvider = {
        RunBuilderRunnerNavigationHandler(buildRunnerData = it)
    },
    private val buildRunnerAnnotations: BuildRunnerAnnotations = BuildRunnerAnnotationsService.getInstance(),
    private val buildRunnerDataBuilder: BuildRunnerDataBuilder = BuildRunnerDataBuilder()
) : LineMarkerProvider {
    override fun getLineMarkerInfo(psiElement: PsiElement): LineMarkerInfo<*>? {

        val isDartFile =
            psiElement.containingFile.virtualFile.fileType == DartFileType.INSTANCE

        if (!isDartFile) {
            return null
        }

        if (psiElement.elementType != DartTokenTypes.AT) {
            return null
        }

        val annotationIdentifier = psiElement.nextSibling.text

        val buildRunnerAnnotation =
            buildRunnerAnnotations.findAnnotation(annotationIdentifier) ?: return null

        val data = buildRunnerDataBuilder
            .setPsiElement(psiElement)
            .setBuildRunnerAnnotation(buildRunnerAnnotation)
            .build()

        return BuildRunnerLineMarkerInfo(
            psiElement = psiElement,
            navigationHandler = createNavigationHandler(data)
        )
    }
}
