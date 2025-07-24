package com.github.frankfuenmayor.dart.buildrunner.codeInsight

import com.github.frankfuenmayor.dart.buildrunner.Icons
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
)
