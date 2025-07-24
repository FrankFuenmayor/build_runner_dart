package com.github.frankfuenmayor.dart.buildrunner.configurations

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.BaseProcessHandler
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.mockk.every
import io.mockk.mockk
import java.io.File


class BuildRunnerCommandLineTest : BasePlatformTestCase() {

    fun `test - run build_runner build`() {
        val processHandler = mockk<BaseProcessHandler<Process>>(relaxed = true)
        val consoleView = mockk<ConsoleView>(relaxed = true)

        lateinit var commandLine : GeneralCommandLine

        val buildRunner = newBuildRunnerBuildCommandLineProvider(
            dartExecutablePath,
            createProcessHandler = {
                commandLine = it
                processHandler
            },
            getBuildRunnerConsoleView = { consoleView }
        )

        val file1 = mockk<File> {
            every { absolutePath } returns "file1"
        }
        val file2 = mockk<File> {
            every { absolutePath } returns "file2"
        }

        buildRunner.runCommandLine(
            project = project,
            workDirectory = File("/somedir"),
            outputFiles = listOf(file1, file2),
            deleteConflictingOutputs = false
        )

        assertEquals(
            "$dartExecutablePath run build_runner build --build-filter file1 --build-filter file2",
            commandLine.commandLineString
        )

        assertEquals(File("/somedir"), commandLine.workDirectory)
    }

    fun `test - run build_runner build --delete-conflicting-outputs`() {
        val processHandler = mockk<BaseProcessHandler<Process>>(relaxed = true)
        val consoleView = mockk<ConsoleView>(relaxed = true)

        lateinit var commandLine : GeneralCommandLine

        val buildRunner = newBuildRunnerBuildCommandLineProvider(
            dartExecutablePath,
            createProcessHandler = {
                commandLine = it
                processHandler
            },
            getBuildRunnerConsoleView = { consoleView }
        )

        buildRunner.runCommandLine(
            project = project,
            workDirectory = File("/somedir"),
            deleteConflictingOutputs = true
        )

        assertEquals(
            "$dartExecutablePath run build_runner build --delete-conflicting-outputs",
            commandLine.commandLineString
        )
        assertEquals(File("/somedir"), commandLine.workDirectory)
    }

    private val dartExecutablePath = "/home/user1/dart"
    private fun newBuildRunnerBuildCommandLineProvider(
        dartExecPath: String,
        createProcessHandler: (GeneralCommandLine) -> BaseProcessHandler<Process> = { mockk() },
        getBuildRunnerConsoleView: (Project) -> ConsoleView? = { mockk() }
    ): BuildRunnerCommandLine {
        return BuildRunnerCommandLine(
            resolveDartExePath = { dartExecPath },
            createProcessHandler = createProcessHandler,
            getBuildRunnerConsoleView = getBuildRunnerConsoleView
        )
    }
}