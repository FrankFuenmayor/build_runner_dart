package com.github.frankfuenmayor.flutterhelper.buildrunner.codeInsight

import com.github.frankfuenmayor.flutterhelper.buildrunner.Icons
import com.github.frankfuenmayor.flutterhelper.buildrunner.codeInsight.RunBuilderRunnerNavigationHandler.Companion.isRunning
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import javax.swing.Icon

class BuildRunnerLineMarkerInfo(
    psiElement: PsiElement,
    navigationHandler: GutterIconNavigationHandler<PsiElement>,
    icon: Icon = Icons.Build
) : LineMarkerInfo<PsiElement>(
    psiElement,
    psiElement.textRange,
    icon,
    { "Run build_runner" },
    navigationHandler,
    GutterIconRenderer.Alignment.LEFT,
    { "" }
) {

    override fun createGutterRenderer(): GutterIconRenderer = object : LineMarkerGutterIconRenderer<PsiElement>(this) {
        override fun getIcon(): Icon {
            return if (this.lineMarkerInfo.element?.isRunning == true) Icons.Stop else Icons.Build
        }
    }
}