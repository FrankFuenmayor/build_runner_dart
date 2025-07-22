package com.github.frankfuenmayor.flutterhelper.buildrunner.configurations

import com.android.tools.idea.run.configuration.execution.println
import com.android.tools.idea.run.configuration.execution.printlnError
import com.github.frankfuenmayor.flutterhelper.buildrunner.Icons
import com.github.frankfuenmayor.flutterhelper.buildrunner.process.BuildRunnerProcessListener
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.BaseProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.jetbrains.lang.dart.sdk.DartSdkUtil
import io.flutter.dart.DartPlugin
import io.flutter.sdk.FlutterSdk
import java.io.File

class BuildRunnerCommandLine(
    val resolveDartExePath: (Project) -> String = ::getDartExePath,
    private val createProcessHandler: (GeneralCommandLine) -> BaseProcessHandler<Process> =
        ProcessHandlerFactory.getInstance()::createColoredProcessHandler,
    private val getBuildRunnerConsoleView: (Project) -> ConsoleView? = ::getBuildRunnerConsoleView
) {

    fun runCommandLine(
        project: Project,
        workDirectory: File,
        outputFiles: List<File> = emptyList(),
        deleteConflictingOutputs: Boolean = false,
        onBuildEnd: () -> Unit = {}
    ) {

        val dartExePath = resolveDartExePath(project)

        val arguments = mutableListOf(
            dartExePath,
            "run",
            "build_runner",
            "build"
        )

        outputFiles.forEach {
            arguments.add("--build-filter")
            arguments.add(it.absolutePath)
        }

        if (deleteConflictingOutputs) {
            arguments.add("--delete-conflicting-outputs")
        }

        val generalCommandLine = GeneralCommandLine(arguments).apply {
            charset = Charsets.UTF_8
            setWorkDirectory(workDirectory)
        }

        val processHandler: BaseProcessHandler<Process> = createProcessHandler(generalCommandLine)
        val consoleView = getBuildRunnerConsoleView(project)
            ?: throw RuntimeException("Console view not found")

        consoleView.clear()

        processHandler.addProcessListener(
            BuildRunnerProcessListener(
                consoleView = consoleView,
                runAgain = {
                    runCommandLine(
                        project = project,
                        workDirectory = workDirectory,
                        deleteConflictingOutputs = true,
                        onBuildEnd = onBuildEnd
                    )
                },
                onBuildEnd = {

                    consoleView.println("")

                    var missingOutputFiles = 0
                    for (file in outputFiles) {
                        if (!file.exists()) {
                            missingOutputFiles++
                            consoleView.printlnError("Output file ${file.name} not generated")
                        }
                    }

                    if (missingOutputFiles > 0) {
                        consoleView.printlnError(
                            "Check your build_runner configuration"
                        )
                    }
                    onBuildEnd()
                }
            )
        )

        processHandler.startNotify()
    }

    companion object {
        private fun getDartExePath(project: Project): String {
            val dartSdkPath = DartPlugin.getDartSdk(project)?.homePath
                ?: FlutterSdk.getFlutterSdk(project)?.dartSdkPath ?: return ""
            return DartSdkUtil.getDartExePath(dartSdkPath)
        }

        private fun getBuildRunnerConsoleView(project: Project): ConsoleView? = ToolWindowManager
            .getInstance(project)
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
    }
}

