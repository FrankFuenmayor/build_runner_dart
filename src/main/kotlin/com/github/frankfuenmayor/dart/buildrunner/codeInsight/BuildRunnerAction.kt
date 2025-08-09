package com.github.frankfuenmayor.dart.buildrunner.codeInsight

import com.github.frankfuenmayor.dart.buildrunner.BuildRunnerDataBuilder
import com.github.frankfuenmayor.dart.buildrunner.configurations.BuildRunnerCommandLine
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findFile

class BuildRunnerAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val directory = event.virtualFile?.takeIf { it.isDirectory } ?: return

        BuildRunnerCommandLine().runCommandLine(
            buildRunnerData = BuildRunnerDataBuilder()
                .setPsiElement(event.getData(CommonDataKeys.PSI_ELEMENT) ?: return)
                .build(),
            deleteConflictingOutputs = true,
            onBuildEnd = {
                event.project?.let { project ->
                    ProjectView.getInstance(project).refresh()
                }
            }
        )

        directory.findFile("pubspec.yaml")
    }

    override fun update(event: AnActionEvent) {
        val isDartProject = event.virtualFile?.takeIf { it.isDirectory }?.findFile("pubspec.yaml")?.exists() == true
        event.presentation.isVisible = isDartProject
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    private val AnActionEvent.virtualFile get() = getData(CommonDataKeys.VIRTUAL_FILE) as? VirtualFile
}