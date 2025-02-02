package com.github.frankfuenmayor.flutterhelper.codeInsight

import com.github.frankfuenmayor.flutterhelper.buildrunner.action.BuildRunnerBuild
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Key
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiElement
import icons.DartIcons
import java.awt.event.MouseEvent
import javax.swing.JMenuItem
import javax.swing.JPopupMenu

class RunBuilderRunnerNavigationHandler(
    private val buildRunnerBuild: BuildRunnerBuild = BuildRunnerBuild()
) : GutterIconNavigationHandler<PsiElement> {

    companion object {
        @JvmStatic
        private var isRunningKey = Key.create<Boolean>("build_runner.isRunning")

        val PsiElement.isRunning: Boolean
            get() = getUserData(isRunningKey) ?: false

        fun PsiElement.setRunning(value: Boolean) = putUserData(isRunningKey, value)
    }


    override fun navigate(e: MouseEvent, psiElement: PsiElement) {

//        if (psiElement.isRunning) {
//            NotificationGroupManager.getInstance()
//                .getNotificationGroup("Flutter Helper Notification Group")
//                .createNotification(
//                    "Flutter helper",
//                    "Build Runner is already running",
//                    NotificationType.WARNING
//                )
//                .notify(psiElement.project);
//            return
//        }
//
//        psiElement.setRunning(true)

//        createPopupMenu().show(e.component, e.x, e.y)


        kotlin.runCatching {
            buildRunnerBuild(
                psiElement.project,
                psiElement.containingFile.virtualFile,
                onBuildEnd = {
//                    psiElement.setRunning(false)
                    ApplicationManager.getApplication().runReadAction {
                        DaemonCodeAnalyzer.getInstance(psiElement.project)
                            .restart(psiElement.containingFile)
                    }
                }
            )
        }
    }

//    private fun createPopupMenu(): JPopupMenu {
//        val popupMenu = JPopupMenu()
//        val menuItem1 = JMenuItem("Action 1")
//        menuItem1.addActionListener {
//             Handle "Action 1" here
//            println("Action 1 selected")
//        }
//        popupMenu.add(menuItem1)
//
//        val menuItem2 = JMenuItem("Action 2", DartIcons.Dart_16)
//        menuItem2.addActionListener {
//             Handle "Action 2" here
//            println("Action 2 selected")
//        }
//        popupMenu.add(menuItem2)
//
//        return popupMenu
//    }
}