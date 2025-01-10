package com.github.frankfuenmayor.flutterhelper.buildrunner

import com.github.frankfuenmayor.flutterhelper.dartSdk
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.util.Key
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiElement
import com.jetbrains.lang.dart.sdk.DartSdkUtil
import com.jetbrains.lang.dart.util.PubspecYamlUtil
import java.awt.event.MouseEvent
import java.io.File

class RunBuilderRunnerNavigationHandler : GutterIconNavigationHandler<PsiElement> {
    override fun navigate(e: MouseEvent?, psiElement: PsiElement) {
        val toolWindow =
            ToolWindowManager.getInstance(psiElement.project)
                .getToolWindow("DartBuildRunnerOutput")

        toolWindow?.title = "Dart Build Runner Output"

        toolWindow?.show()

        val dartSdkPath = psiElement.project.dartSdk ?: return
        val dartExePath = DartSdkUtil.getDartExePath(dartSdkPath)

        val yamlFile =
            PubspecYamlUtil.findPubspecYamlFile(
                psiElement.project,
                psiElement.containingFile.virtualFile
            )
                ?: return
        val generalCommandLine =
            GeneralCommandLine(dartExePath, "run", "build_runner", "build")

        generalCommandLine.charset = Charsets.UTF_8
        generalCommandLine.workDirectory = File(yamlFile.parent.path)

        val processHandler =
            ProcessHandlerFactory.getInstance()
                .createColoredProcessHandler(generalCommandLine)

        val consoleView =
            ShellScriptToolWindowManager.getConsoleView(psiElement.project)

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
        })

        processHandler.startNotify()
    }
}
