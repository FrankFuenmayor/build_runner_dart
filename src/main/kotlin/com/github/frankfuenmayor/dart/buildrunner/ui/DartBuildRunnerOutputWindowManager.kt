package com.github.frankfuenmayor.dart.buildrunner.ui

import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project

object DartBuildRunnerOutputWindowManager {
    private val consoleViews = mutableMapOf<Project, ConsoleView>()

    fun register(project: Project, consoleView: ConsoleView) {
        consoleViews[project] = consoleView
    }
}