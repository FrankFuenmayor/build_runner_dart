package com.github.frankfuenmayor.dart.buildrunner.configurations

import com.github.frankfuenmayor.dart.buildrunner.BuildRunnerData
import com.github.frankfuenmayor.dart.buildrunner.ignoreBuildRunnerAnnotationsMissingParts
import com.github.frankfuenmayor.dart.buildrunner.process.BuildRunnerProcessListener
import com.github.frankfuenmayor.dart.buildrunner.ui.DartBuildRunnerOutputWindow.Companion.DART_BUILD_RUNNER_TOOL_WINDOW_ID
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.process.BaseProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.ContentFactory
import com.jetbrains.lang.dart.sdk.DartSdk
import com.jetbrains.lang.dart.sdk.DartSdkUtil
import java.io.File

class BuildRunnerCommandLine(
    val resolveDartExePath: (Project) -> String = ::getDartExePath,
    private val createProcessHandler: (GeneralCommandLine) -> BaseProcessHandler<Process> =
        ProcessHandlerFactory.getInstance()::createColoredProcessHandler,
    private val getBuildRunnerConsoleView: (Project, dartProjectName: String, filename: String) -> ConsoleView? = ::getBuildRunnerConsoleView
) {

    fun runCommandLine(
        buildRunnerData: BuildRunnerData,
        deleteConflictingOutputs: Boolean = false,
        outputFiles: List<File> = emptyList(),
        onBuildEnd: () -> Unit = {}
    ) {

        val dartExePath = resolveDartExePath(buildRunnerData.project)

        val arguments = mutableListOf(
            dartExePath,
            "run",
            "build_runner",
            "build"
        )

        if (deleteConflictingOutputs) {
            arguments.add("--delete-conflicting-outputs")
        } else {
            outputFiles.forEach { outputFile ->
                arguments.add("--build-filter")
                arguments.add(outputFile.relativeTo(buildRunnerData.projectFolder).path)
            }
        }

        val generalCommandLine = GeneralCommandLine(arguments).apply {
            charset = Charsets.UTF_8
            setWorkDirectory(buildRunnerData.projectFolder)
        }

        val processHandler: BaseProcessHandler<Process> = createProcessHandler(generalCommandLine)
        val consoleView = getBuildRunnerConsoleView(
            buildRunnerData.project,
            buildRunnerData.dartProjectName,
            buildRunnerData.filename
        )
            ?: throw RuntimeException("Console view not found")

        consoleView.clear()

        if (buildRunnerData.missingBuildRunnerDependency) {
            consoleView.println("💥 ERROR: build_runner dev_dependency is missing 💥")
            consoleView.println("run cancelled.")

            return
        }

        processHandler.addProcessListener(
            BuildRunnerProcessListener(
                consoleView = consoleView,
                runAgain =
                    {
                        runCommandLine(
                            buildRunnerData,
                            deleteConflictingOutputs = true,
                            onBuildEnd = onBuildEnd
                        )
                    },
                onBuildEnd =
                    {

                        consoleView.println("")

                        var missingOutputFiles = 0

                        val outputFiles = buildRunnerData.outputFiles.filterNot {
                            buildRunnerData.file.ignoreBuildRunnerAnnotationsMissingParts().contains(it.name)
                        }

                        for (file in outputFiles) {
                            if (!file.exists()) {
                                missingOutputFiles++
                                consoleView.println("⚠️ Output file ${file.name} not generated")
                            }
                        }

                        if (missingOutputFiles > 0) {
                            consoleView.println(
                                "⚠️ Check your build_runner configuration"
                            )
                        }

                        onBuildEnd()
                    }
            )
        )

        processHandler.startNotify()
    }

    companion object {
        private fun getDartExePath(project: Project): String = DartSdk.getDartSdk(project)?.let {
            DartSdkUtil.getDartExePath(it)
        } ?: throw RuntimeException("Dart SDK not found")

        private fun getBuildRunnerConsoleView(
            project: Project,
            dartProjectName: String,
            filename: String
        ): ConsoleView? {
            val toolWindow = ToolWindowManager
                .getInstance(project)
                .getToolWindow(DART_BUILD_RUNNER_TOOL_WINDOW_ID) ?: return null

            toolWindow.activate {}

            val contentManager = toolWindow.contentManager
            val displayName = "$filename [$dartProjectName]"
            var content = contentManager.contents.firstOrNull {
                it.displayName == displayName
            }

            if (content == null) {
                val consoleView = ConsoleViewImpl(project, true)

                content = ContentFactory.getInstance().createContent(
                    consoleView.component,
                    displayName,
                    false
                )

                contentManager.addContent(content)
            }

            contentManager.setSelectedContent(content)

            return content.component as ConsoleView
        }
    }
}

private fun ConsoleView.println(string: String) {
    print(string + "\n", ConsoleViewContentType.NORMAL_OUTPUT)
}

