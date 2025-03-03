package com.github.frankfuenmayor.flutterhelper.buildrunner.configurations

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.io.File


class BuildRunnerBuildCommandLineProviderTest : BasePlatformTestCase() {

    fun `test - Provide GeneralCommandLine for specific files`() {
        val provider = newProvider(dartExecPathForTest)

        val commandLine =
            provider.getCommandLine(
                project = project,
                workDirectory = File("/somedir"),
                outputFiles = listOf("file1", "file2"),
                deleteConflictingOutputs = false
            )

        assertEquals(
            "$dartExecPathForTest run build_runner build --build-filter file1 --build-filter file2",
            commandLine.commandLineString
        )
        assertEquals(File("/somedir"), commandLine.workDirectory)
    }

    fun `test - Provide GeneralCommandLine with delete conflicting outputs flag`() {
        val provider = newProvider(dartExecPathForTest)

        val commandLine =
            provider.getCommandLine(
                project = project,
                workDirectory = File("/somedir"),
                deleteConflictingOutputs = true
            )

        assertEquals(
            "$dartExecPathForTest run build_runner build --delete-conflicting-outputs",
            commandLine.commandLineString
        )
        assertEquals(File("/somedir"), commandLine.workDirectory)
    }

    private val dartExecPathForTest = "/home/user1/dart"
    private fun newProvider(dartExecPath: String): BuildRunnerBuildCommandLineProvider {

        return BuildRunnerBuildCommandLineProvider(resolveDartExePath = { dartExecPath })
    }
}