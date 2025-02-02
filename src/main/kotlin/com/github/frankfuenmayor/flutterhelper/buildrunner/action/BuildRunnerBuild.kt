package com.github.frankfuenmayor.flutterhelper.buildrunner.action

import com.github.frankfuenmayor.flutterhelper.dartSdk
import com.github.frankfuenmayor.flutterhelper.ui.DartBuildRunnerOutputWindowManager
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.*
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindowManager
import com.jetbrains.lang.dart.sdk.DartSdkUtil
import com.jetbrains.lang.dart.util.PubspecYamlUtil
import java.io.File

typealias FindPubspecYamlFile = (project: Project, virtualFile: VirtualFile) -> VirtualFile?

class BuildRunnerBuild(
    private val commandLineProvider: BuildRunnerBuildCommandLineProvider = BuildRunnerBuildCommandLineProvider(),
    private val findPubspecYamlFile: FindPubspecYamlFile = PubspecYamlUtil::findPubspecYamlFile
) {
    operator fun invoke(
        project: Project, virtualFile: VirtualFile, onBuildEnd: () -> Unit
    ) {

        val yamlFile = findPubspecYamlFile(project, virtualFile) ?: return

        val generalCommandLine =
            commandLineProvider.getCommandLine(project, File(yamlFile.parent.path))
                ?: return

        val processHandler: BaseProcessHandler<Process> =
            ProcessHandlerFactory.getInstance()
                .createColoredProcessHandler(generalCommandLine)

        val consoleView =
            DartBuildRunnerOutputWindowManager.getConsoleView(project)

        val toolWindow =
            ToolWindowManager.getInstance(project)
                .getToolWindow("DartBuildRunnerOutput")

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

class BuildRunnerBuildCommandLineProvider {

    fun getCommandLine(project: Project, workDirectory: File): GeneralCommandLine? {
        val dartSdkPath = project.dartSdk ?: return null
        val dartExePath = DartSdkUtil.getDartExePath(dartSdkPath)

        return GeneralCommandLine(
            dartExePath,
            "run",
            "build_runner",
            "build"
        ).apply {
            charset = Charsets.UTF_8
            setWorkDirectory(workDirectory)
        }
    }
}
