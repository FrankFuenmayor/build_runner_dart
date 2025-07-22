package com.github.frankfuenmayor.flutterhelper.buildrunner.configurations

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.mockk.every
import io.mockk.mockk
import java.io.File


class BuildRunnerBuildCommandLineProviderTest : BasePlatformTestCase() {

    fun `test - Provide GeneralCommandLine for specific files`() {
        val provider = newBuildRunnerBuildCommandLineProvider(dartExecutablePath)

        val file1 = mockk<File> {
            every { absolutePath } returns "file1"
        }
        val file2 = mockk<File> {
            every { absolutePath } returns "file2"
        }

        val commandLine = provider.getCommandLine(
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

    fun `test - Provide GeneralCommandLine with delete conflicting outputs flag`() {
        val provider = newBuildRunnerBuildCommandLineProvider(dartExecutablePath)

        val commandLine =
            provider.getCommandLine(
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
    private fun newBuildRunnerBuildCommandLineProvider(dartExecPath: String): BuildRunnerBuildCommandLineProvider {

        return BuildRunnerBuildCommandLineProvider(resolveDartExePath = { dartExecPath })
    }
}