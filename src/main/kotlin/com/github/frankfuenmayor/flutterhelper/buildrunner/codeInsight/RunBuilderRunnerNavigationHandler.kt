package com.github.frankfuenmayor.flutterhelper.buildrunner.codeInsight

import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerData
import com.github.frankfuenmayor.flutterhelper.buildrunner.configurations.BuildRunnerCommandLine
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.jetbrains.lang.dart.DartTokenTypes
import java.awt.Point
import java.awt.event.MouseEvent
import java.io.File

class RunBuilderRunnerNavigationHandler(
    private val buildRunnerData: BuildRunnerData,
    private val buildRunnerCommandLine: BuildRunnerCommandLine = BuildRunnerCommandLine(),
    private val createPopupMenu: CreateBuildRunnerPopupMenu = CreateBuildRunnerPopupMenu(),
    private val refreshGutterIcons: (PsiElement) -> Unit = ::refreshGutterIcons
) : GutterIconNavigationHandler<PsiElement> {

    companion object {
        @JvmStatic
        private var isRunningKey = Key.create<Boolean>("build_runner.isRunning")

        val PsiElement.isRunning: Boolean get() = getUserData(isRunningKey) ?: false

        fun PsiElement.setRunning(value: Boolean) = putUserData(isRunningKey, value)

        private fun refreshGutterIcons(psiElement: PsiElement) {
            val project = psiElement.project
            ApplicationManager.getApplication().invokeLater {
                DaemonCodeAnalyzer
                    .getInstance(project)
                    .restart(psiElement.containingFile)
            }
        }
    }

    override fun navigate(e: MouseEvent, psiElement: PsiElement) {
        assert(psiElement.elementType == DartTokenTypes.AT)

        createPopupMenu(buildRunnerData) { deleteConflictingOutputs, outputFiles ->
            onBuildOptionSelected(psiElement, outputFiles, deleteConflictingOutputs)
        }.showInScreenCoordinates(e.component, Point(e.xOnScreen, e.yOnScreen))
    }

    private fun onBuildOptionSelected(
        psiElement: PsiElement,
        outputFiles: List<File>,
        deleteConflictingOutputs: Boolean
    ) {
        psiElement.setRunning(true)
        buildRunnerCommandLine.runCommandLine(
            project = psiElement.project,
            workDirectory = buildRunnerData.projectFolder,
            outputFiles = outputFiles,
            deleteConflictingOutputs = deleteConflictingOutputs,
            onBuildEnd = {
                psiElement.setRunning(false)
                refreshGutterIcons(psiElement)
            },
        )
    }
}
