package com.github.frankfuenmayor.flutterhelper.buildrunner

import com.github.frankfuenmayor.flutterhelper.dartSdk
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiElement
import com.jetbrains.lang.dart.sdk.DartSdkUtil
import com.jetbrains.lang.dart.util.PubspecYamlUtil
import icons.DartIcons
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.JMenuItem
import javax.swing.JPopupMenu

class RunBuilderRunnerNavigationHandler : GutterIconNavigationHandler<PsiElement> {

    companion object {
        @JvmStatic
        private var isRunningKey = Key.create<Boolean>("build_runner.isRunning")

        val PsiElement.isRunning: Boolean
            get() = getUserData(isRunningKey) ?: false

        fun PsiElement.setRunning(value: Boolean) = putUserData(isRunningKey, value)
    }


    override fun navigate(e: MouseEvent, psiElement: PsiElement) {

        if (psiElement.isRunning) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Flutter Helper Notification Group")
                .createNotification(
                    "Flutter helper",
                    "Build Runner is already running",
                    NotificationType.WARNING
                )
                .notify(psiElement.project);
            return
        }

        psiElement.setRunning(true)

        createPopupMenu().show(e.component, e.x, e.y)

        val toolWindow =
            ToolWindowManager.getInstance(psiElement.project)
                .getToolWindow("DartBuildRunnerOutput")

        toolWindow?.title = "Dart Build Runner Output"

        toolWindow?.show()

        kotlin.runCatching {
            runBuildRunner(
                psiElement.project,
                psiElement.containingFile.virtualFile,
                onEnd = {
                    psiElement.setRunning(false)
                    ApplicationManager.getApplication().runReadAction {
                        DaemonCodeAnalyzer.getInstance(psiElement.project).restart(psiElement.containingFile)
                    }
                }
            )
        }
    }

    private fun createPopupMenu(): JPopupMenu {
        val popupMenu = JPopupMenu()
        val menuItem1 = JMenuItem("Action 1")
        menuItem1.addActionListener {
            // Handle "Action 1" here
            println("Action 1 selected")
        }
        popupMenu.add(menuItem1)

        val menuItem2 = JMenuItem("Action 2", DartIcons.Dart_16,)
        menuItem2.addActionListener {
            // Handle "Action 2" here
            println("Action 2 selected")
        }
        popupMenu.add(menuItem2)

        return popupMenu
    }


    private fun runBuildRunner(project: Project, virtualFile: VirtualFile, onEnd: () -> Unit = {}) {

        val dartSdkPath = project.dartSdk ?: return
        val dartExePath = DartSdkUtil.getDartExePath(dartSdkPath)

        val yamlFile = PubspecYamlUtil.findPubspecYamlFile(project, virtualFile) ?: return
        val generalCommandLine = GeneralCommandLine(dartExePath, "run", "build_runner", "build")

        generalCommandLine.charset = Charsets.UTF_8
        generalCommandLine.workDirectory = File(yamlFile.parent.path)

        val processHandler =
            ProcessHandlerFactory.getInstance()
                .createColoredProcessHandler(generalCommandLine)

        val consoleView =
            DartBuildRunnerOutputWindowManager.getConsoleView(project)

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
                onEnd()
            }
        })

        processHandler.startNotify()
    }
}
