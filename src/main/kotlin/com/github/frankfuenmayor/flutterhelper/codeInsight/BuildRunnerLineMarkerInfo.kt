package com.github.frankfuenmayor.flutterhelper.codeInsight

import com.github.frankfuenmayor.flutterhelper.codeInsight.RunBuilderRunnerNavigationHandler.Companion.isRunning
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.ExpUiIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import javax.swing.Icon

class BuildRunnerLineMarkerInfo(
    psiElement: PsiElement,
    navigationHandler: GutterIconNavigationHandler<PsiElement> = RunBuilderRunnerNavigationHandler(),
) : LineMarkerInfo<PsiElement>(
    psiElement,
    psiElement.textRange,
    ExpUiIcons.Run.Run,
    { "dart pub run build_runner build" },
    navigationHandler,
    GutterIconRenderer.Alignment.LEFT,
    { "" }
) {

    override fun createGutterRenderer(): GutterIconRenderer {
        return object : LineMarkerGutterIconRenderer<PsiElement>(this) {
            override fun getIcon(): Icon {
                return if (this.lineMarkerInfo.element?.isRunning == true) ExpUiIcons.Run.Stop else ExpUiIcons.Run.Run
            }
        }
    }
}