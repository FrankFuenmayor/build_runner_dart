package com.github.frankfuenmayor.flutterhelper.buildrunner.configurations

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import junit.framework.TestCase
import java.io.File


class BuildRunnerBuildCommandLineProviderTest : BasePlatformTestCase() {

    fun `test - TBD`() {
        val provider =
            BuildRunnerBuildCommandLineProvider(resolveDartExePath = { "/home/user1/dart" })

        val commandLine =
            provider.getCommandLine(
                project = project,
                File("/somedir"),
                deleteConflictingOutputs = false
            )

        TestCase.assertEquals(
            "/home/user1/dart run build_runner build",
            commandLine.commandLineString
        )
    }
}