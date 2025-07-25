package com.github.frankfuenmayor.dart.buildrunner.ui

import com.github.frankfuenmayor.dart.buildrunner.Icons
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory

class DartBuildRunnerOutputWindow : ToolWindowFactory, DumbAware {

    companion object
    {
        const val DART_BUILD_RUNNER_TOOL_WINDOW_ID = "build_runner"
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.setIcon(Icons.Build)
    }
}