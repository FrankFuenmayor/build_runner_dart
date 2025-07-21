package com.github.frankfuenmayor.flutterhelper.buildrunner.codeInsight

import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerAnnotation
import com.github.frankfuenmayor.flutterhelper.buildrunner.action.BuildRunnerBuild
import com.github.frankfuenmayor.flutterhelper.buildrunner.codeInsight.RunBuilderRunnerNavigationHandler.Companion.setRunning
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.jetbrains.lang.dart.DartTokenTypes
import com.jetbrains.lang.dart.util.PubspecYamlUtil
import java.awt.Point
import java.awt.event.MouseEvent
import java.io.File

typealias FindPubspecYamlContainingFolder = (psiElement: PsiElement) -> File?

class RunBuilderRunnerNavigationHandler(
    private val buildRunnerAnnotation: BuildRunnerAnnotation,
    private val buildRunnerBuild: BuildRunnerBuild = BuildRunnerBuild(),
    private val createPopupMenu: CreateBuildRunnerPopupMenu = CreateBuildRunnerPopupMenu(),
    private val findPubspecYamlContainingFolder: FindPubspecYamlContainingFolder = ::findPubspecYamlContainingFolder,
    private val refreshGutterIcons: (PsiElement) -> Unit = ::refreshGutterIcons
) : GutterIconNavigationHandler<PsiElement> {

    companion object {
        @JvmStatic
        private var isRunningKey = Key.create<Boolean>("build_runner.isRunning")

        val PsiElement.isRunning: Boolean get() = getUserData(isRunningKey) ?: false

        fun PsiElement.setRunning(value: Boolean) = putUserData(isRunningKey, value)
    }

    override fun navigate(e: MouseEvent, psiElement: PsiElement) {
        assert(psiElement.elementType == DartTokenTypes.AT)

        val workDirectory = findPubspecYamlContainingFolder(psiElement) ?: return

        createPopupMenu(
            psiElement = psiElement,
            buildRunnerAnnotation = buildRunnerAnnotation
        ) { deleteConflictingOutputs, buildFilter ->
            psiElement.setRunning(true)
            buildRunnerBuild(
                project = psiElement.project,
                workDirectory = workDirectory,
                buildFilter = buildFilter,
                deleteConflictingOutputs = deleteConflictingOutputs,
                onBuildEnd = {
                    refreshGutterIcons(psiElement)

                    var expectedOutputExist = true
                    if (buildFilter.isNotEmpty()) {
                        expectedOutputExist = buildFilter.all { file ->
                            File(workDirectory, file).exists()
                        }
                    }

                    if (!expectedOutputExist) {

                        NotificationGroupManager.getInstance()
                            .getNotificationGroup("Dart build_runner notification group")
                            .createNotification("Build finished with error", NotificationType.ERROR)
                            .notify(psiElement.project);

                    }

                },
            )
        }?.showInScreenCoordinates(e.component, Point(e.xOnScreen, e.yOnScreen))
    }


}

private fun findPubspecYamlContainingFolder(psiElement: PsiElement): File? = PubspecYamlUtil
    .findPubspecYamlFile(psiElement.project, psiElement.containingFile.virtualFile)
    ?.parent
    ?.path
    ?.let { File(it) }

private fun refreshGutterIcons(psiElement: PsiElement) {
    psiElement.setRunning(false)
    val project = psiElement.project
    ApplicationManager.getApplication().invokeLater {
        DaemonCodeAnalyzer
            .getInstance(project)
            .restart(psiElement.containingFile)
    }
}