package com.github.frankfuenmayor.flutterhelper.buildrunner.process

import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.ui.ConsoleView
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder

class BuildRunnerProcessListenerTest : BasePlatformTestCase() {


    fun `test - print text available`() {
        val consoleView = mockk<ConsoleView>(relaxed = true)

        val processListener = BuildRunnerProcessListener(
            runAgain = {},
            onBuildEnd = {},
            consoleView = consoleView
        )

        val processEvent = mockk<ProcessEvent>().apply {
            every { exitCode } returns 1
        }

        processListener.processTerminated(processEvent)

        verifyOrder {
            consoleView.print(
                match { it.startsWith("\nBuild finished with error") }, any()
            )
            consoleView.printHyperlink(
                match { it.startsWith("run again with flag --delete-conflicting-outputs") }, any()
            )
        }
    }
}