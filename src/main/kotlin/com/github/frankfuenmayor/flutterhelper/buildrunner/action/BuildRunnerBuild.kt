package com.github.frankfuenmayor.flutterhelper.buildrunner.action

import com.github.frankfuenmayor.flutterhelper.buildrunner.configurations.BuildRunnerBuildCommandLineProvider
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.*
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindowManager
import com.jetbrains.lang.dart.util.PubspecYamlUtil
import java.awt.Color
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

        val toolWindow =
            ToolWindowManager.getInstance(project)
                .getToolWindow("Build Runner")

        toolWindow?.title = "Dart Build Runner Output"
        toolWindow?.show()

        val consoleView = toolWindow?.contentManager?.contents?.firstOrNull()?.component as ConsoleView?

        consoleView?.clear()

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

                val exitWithErrors = event.exitCode != 0

                if (exitWithErrors) {
                    consoleView?.apply {
                        print(
                            "\nBuild finished with error (exit code ${event.exitCode}) 💥, ",
                            ConsoleViewContentType.ERROR_OUTPUT
                        )
                        printHyperlink("run again with flag --delete-conflicting-outputs\n") {
                            clear()
                            invoke(project, virtualFile, buildFilter = emptyList(), true, onBuildEnd)
                        }
                    }
                } else {
                    consoleView?.print("Build finished successfully ✅\n", SUCCESS_OUTPUT)
                }


                onBuildEnd()
            }
        })
        processHandler.startNotify()
    }
}

private val SUCCESS_OUTPUT = ConsoleViewContentType(
    "SUCCESS_OUTPUT",
    TextAttributesKey.createTextAttributesKey(
        "SUCCESS_OUTPUT"
    ).apply {
        TextAttributes().apply {
            foregroundColor = Color(0x2D, 0x8C, 0x3C) // Green color in RGB
        }
    }
)


