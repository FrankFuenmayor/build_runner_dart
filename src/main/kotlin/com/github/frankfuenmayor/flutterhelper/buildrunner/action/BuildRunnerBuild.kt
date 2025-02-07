package com.github.frankfuenmayor.flutterhelper.buildrunner.action

import com.github.frankfuenmayor.flutterhelper.buildrunner.configurations.BuildRunnerBuildCommandLineProvider
import com.github.frankfuenmayor.flutterhelper.ui.DartBuildRunnerOutputWindowManager
import com.intellij.execution.process.*
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindowManager
import com.jetbrains.lang.dart.util.PubspecYamlUtil
import java.io.File

typealias FindPubspecYamlFile = (project: Project, virtualFile: VirtualFile) -> VirtualFile?

class BuildRunnerBuild(
    private val commandLineProvider: BuildRunnerBuildCommandLineProvider = BuildRunnerBuildCommandLineProvider(),
    private val findPubspecYamlFile: FindPubspecYamlFile = PubspecYamlUtil::findPubspecYamlFile
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
            ProcessHandlerFactory.getInstance()
                .createColoredProcessHandler(generalCommandLine)

        val consoleView =
            DartBuildRunnerOutputWindowManager.getConsoleView(project)

        val toolWindow =
            ToolWindowManager.getInstance(project)
                .getToolWindow("Build Runner")

        toolWindow?.title = "Dart Build Runner Output"

        toolWindow?.show()


        processHandler.addProcessListener(object : ProcessAdapter() {
            override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                val text = event.text
                val contentType = when (outputType) {
                    ProcessOutputTypes.STDOUT -> ConsoleViewContentType.NORMAL_OUTPUT
                    ProcessOutputTypes.STDERR -> ConsoleViewContentType.ERROR_OUTPUT
                    else -> ConsoleViewContentType.SYSTEM_OUTPUT
                }
                consoleView?.print(text, contentType)
            }

            override fun processTerminated(event: ProcessEvent) {
                onBuildEnd()
            }
        })
        processHandler.startNotify()
    }
}

