package com.github.frankfuenmayor.flutterhelper.codeInsight

import com.github.frankfuenmayor.flutterhelper.buildrunner.action.BuildRunnerBuild
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.jetbrains.lang.dart.DartTokenTypes
import java.awt.event.MouseEvent


class RunBuilderRunnerNavigationHandler(
    private val buildRunnerBuild: BuildRunnerBuild = BuildRunnerBuild(),
    private val createPopupMenu: CreateBuildRunnerPopupMenu = CreateBuildRunnerPopupMenu()
) : GutterIconNavigationHandler<PsiElement> {

    companion object {
        @JvmStatic
        private var isRunningKey = Key.create<Boolean>("build_runner.isRunning")

        val PsiElement.isRunning: Boolean
            get() = getUserData(isRunningKey) ?: false

        fun PsiElement.setRunning(value: Boolean) = putUserData(isRunningKey, value)
    }

    override fun navigate(e: MouseEvent, psiElement: PsiElement) {
        assert(psiElement.elementType == DartTokenTypes.AT)

        createPopupMenu(psiElement) { deleteConflictingOutputs, buildFilter ->
            buildRunnerBuild(
                project = psiElement.project,
                virtualFile = psiElement.containingFile.virtualFile,
                buildFilter = buildFilter,
                deleteConflictingOutputs = deleteConflictingOutputs
            )
        }.show(e.component, e.x, e.y)
    }
}
