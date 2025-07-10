package com.github.frankfuenmayor.flutterhelper.buildrunner.process

import com.github.frankfuenmayor.flutterhelper.ui.SUCCESS_OUTPUT
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.util.Key

class BuildRunnerProcessListener(
    private val consoleView: ConsoleView,
    private val runAgain: () -> Unit,
    private val onBuildEnd: () -> Unit
) : ProcessListener {

    override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
        val text = event.text
        val contentType = when (outputType) {
            ProcessOutputTypes.STDOUT -> ConsoleViewContentType.NORMAL_OUTPUT
            ProcessOutputTypes.STDERR -> ConsoleViewContentType.ERROR_OUTPUT
            else -> ConsoleViewContentType.SYSTEM_OUTPUT
        }
        consoleView.print(text, contentType)
    }

    override fun processTerminated(event: ProcessEvent) {

        val exitWithErrors = event.exitCode != 0

        if (exitWithErrors) {
            consoleView.apply {
                print(
                    "\nBuild finished with error (exit code ${event.exitCode}) -> ",
                    ConsoleViewContentType.ERROR_OUTPUT
                )
                printHyperlink("run again with flag --delete-conflicting-outputs\n") {
                    clear()
                    runAgain()
                }
            }
        } else {
            consoleView.print("Build finished successfully\n", SUCCESS_OUTPUT)
        }
        onBuildEnd()
    }
}
