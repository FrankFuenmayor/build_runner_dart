package com.github.frankfuenmayor.dart.buildrunner.ui

import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class DartBuildRunnerOutputWindow : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val consoleView = ConsoleViewImpl(project, true)
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(consoleView.component, "", false)
        toolWindow.contentManager.addContent(content)
    }
}