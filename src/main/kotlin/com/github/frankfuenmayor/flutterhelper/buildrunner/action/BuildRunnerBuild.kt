package com.github.frankfuenmayor.flutterhelper.buildrunner.action

import com.github.frankfuenmayor.flutterhelper.buildrunner.configurations.BuildRunnerBuildCommandLineProvider
import com.github.frankfuenmayor.flutterhelper.buildrunner.process.BuildRunnerProcessListener
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.BaseProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindowManager
import com.jetbrains.lang.dart.util.PubspecYamlUtil
import java.io.File

typealias FindPubspecYamlFile = (project: Project, virtualFile: VirtualFile) -> VirtualFile?

class BuildRunnerBuild(
    private val commandLineProvider: BuildRunnerBuildCommandLineProvider = BuildRunnerBuildCommandLineProvider(),
    private val findPubspecYamlFile: FindPubspecYamlFile = PubspecYamlUtil::findPubspecYamlFile,
    private val createProcessHandler: (GeneralCommandLine) -> BaseProcessHandler<Process> = {
        ProcessHandlerFactory.getInstance().createColoredProcessHandler(it)
    }
) {
    operator fun invoke(
        project: Project,
        virtualFile: VirtualFile,
        buildFilter: List<String> = emptyList(),
        deleteConflictingOutputs: Boolean = false,
        onBuildEnd: () -> Unit = {}
    ) {

        val yamlFile = findPubspecYamlFile(project, virtualFile) ?: return

        val workDirectory = File(yamlFile.parent.path)

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
                        virtualFile = virtualFile,
                        deleteConflictingOutputs = true,
                        onBuildEnd = onBuildEnd
                    )
                },
                onBuildEnd = {

                    onBuildEnd()
                }
            )
        )
        processHandler.startNotify()
    }
}

private fun Project.getConsoleView(): ConsoleView? {
    val toolWindow =
        ToolWindowManager.getInstance(this)
            .getToolWindow("Build Runner")

    toolWindow?.title = "Dart Build Runner Output"
    toolWindow?.show()
    return toolWindow
        ?.contentManager
        ?.contents
        ?.firstOrNull()
        ?.component as ConsoleView?
}
