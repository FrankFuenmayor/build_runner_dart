package com.github.frankfuenmayor.flutterhelper.codeInsight

import com.github.frankfuenmayor.flutterhelper.buildrunner.Icons
import com.github.frankfuenmayor.flutterhelper.codeInsight.RunBuilderRunnerNavigationHandler.Companion.isRunning
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons.Actions.Execute
import com.intellij.icons.AllIcons.Run.Stop
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import javax.swing.Icon

class BuildRunnerLineMarkerInfo(
    psiElement: PsiElement,
    navigationHandler: GutterIconNavigationHandler<PsiElement>,
) : LineMarkerInfo<PsiElement>(
    psiElement,
    psiElement.textRange,
    Icons.Run,
    { "Run build_runner build" },
    navigationHandler,
    GutterIconRenderer.Alignment.LEFT,
    { "" }
) {

    override fun createGutterRenderer(): GutterIconRenderer = object : LineMarkerGutterIconRenderer<PsiElement>(this) {
        override fun getIcon(): Icon {
            return if (this.lineMarkerInfo.element?.isRunning == true) Stop else Execute
        }
    }
}