package com.github.frankfuenmayor.flutterhelper.codeInsight

import com.github.frankfuenmayor.flutterhelper.codeInsight.RunBuilderRunnerNavigationHandler.Companion.isRunning
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.icons.AllIcons.Run.Restart
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
    AllIcons.Run.Restart,
    { "Run build_runner build" },
    navigationHandler,
    GutterIconRenderer.Alignment.LEFT,
    { "" }
) {

    override fun createGutterRenderer(): GutterIconRenderer = object : LineMarkerGutterIconRenderer<PsiElement>(this) {
        override fun getIcon(): Icon {
            return if (this.lineMarkerInfo.element?.isRunning == true) Stop else Restart
        }
    }
}