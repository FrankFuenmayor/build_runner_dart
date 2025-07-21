package com.github.frankfuenmayor.flutterhelper.buildrunner.action

import com.github.frankfuenmayor.flutterhelper.buildrunner.Icons
import com.github.frankfuenmayor.flutterhelper.buildrunner.configurations.BuildRunnerBuildCommandLineProvider
import com.github.frankfuenmayor.flutterhelper.buildrunner.process.BuildRunnerProcessListener
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.BaseProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import java.io.File

class BuildRunnerBuild(
    private val commandLineProvider: BuildRunnerBuildCommandLineProvider = BuildRunnerBuildCommandLineProvider(),
    private val createProcessHandler: (GeneralCommandLine) -> BaseProcessHandler<Process> =
        ProcessHandlerFactory.getInstance()::createColoredProcessHandler
) {
    operator fun invoke(
        project: Project,
        workDirectory: File,
        buildFilter: List<String> = emptyList(),
        deleteConflictingOutputs: Boolean = false,
        onBuildEnd: () -> Unit = {}
    ) {
        val generalCommandLine =
            commandLineProvider.getCommandLine(
                project = project,
                workDirectory = workDirectory,
                outputFiles = buildFilter,
                deleteConflictingOutputs = deleteConflictingOutputs
            )

        val processHandler: BaseProcessHandler<Process> =
            createProcessHandler(generalCommandLine)

        val consoleView = project.getConsoleView()
            ?: throw RuntimeException("Console view not found")

        consoleView.clear()

        processHandler.addProcessListener(
            BuildRunnerProcessListener(
                consoleView = consoleView,
                runAgain = {
                    invoke(
                        project = project,
                        workDirectory = workDirectory,
                        deleteConflictingOutputs = true,
                        onBuildEnd = onBuildEnd
                    )
                },
                onBuildEnd = { onBuildEnd() }
            )
        )
        processHandler.startNotify()
    }
}

private fun Project.getConsoleView(): ConsoleView? = ToolWindowManager
    .getInstance(this)
    .getToolWindow("dart build_runner")
    ?.let { toolWindow ->
        toolWindow.show()
        toolWindow.setIcon(Icons.Build)
        toolWindow
            .contentManager
            .contents
            .firstOrNull()
            ?.component as ConsoleView?
    }
