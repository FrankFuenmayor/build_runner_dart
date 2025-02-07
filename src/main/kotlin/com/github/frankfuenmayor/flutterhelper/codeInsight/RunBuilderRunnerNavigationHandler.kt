package com.github.frankfuenmayor.flutterhelper.codeInsight

import com.github.frankfuenmayor.flutterhelper.buildrunner.action.BuildRunnerBuild
import com.github.frankfuenmayor.flutterhelper.settings.BuildRunnerBuildKnownAnnotations
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.jetbrains.lang.dart.DartTokenTypes
import com.jetbrains.lang.dart.util.PubspecYamlUtil
import icons.DartIcons
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.JMenuItem
import javax.swing.JPopupMenu
import javax.swing.JSeparator

class RunBuilderRunnerNavigationHandler(
    private val createPopupMenu: (PsiElement) -> JPopupMenu = { psiElement ->
        createPopupMenuForElement(
            psiElement
        )
    }
) : GutterIconNavigationHandler<PsiElement> {

    companion object {
        @JvmStatic
        private var isRunningKey = Key.create<Boolean>("build_runner.isRunning")

        val PsiElement.isRunning: Boolean
            get() = getUserData(isRunningKey) ?: false

        fun PsiElement.setRunning(value: Boolean) = putUserData(isRunningKey, value)
    }

    override fun navigate(e: MouseEvent, psiElement: PsiElement) {
        assert(psiElement.elementType == DartTokenTypes.AT)

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

        createPopupMenu(psiElement).show(e.component, e.x, e.y)
    }
}


private fun createPopupMenuForElement(
    psiElement: PsiElement,
    buildRunnerBuild: BuildRunnerBuild = BuildRunnerBuild(),
    knownAnnotations: BuildRunnerBuildKnownAnnotations = BuildRunnerBuildKnownAnnotations()
): JPopupMenu {

    val annotationIdentifier = psiElement.nextSibling.text

    val buildRunnerAnnotation =
        knownAnnotations.findAnnotation(annotationIdentifier) ?: return JPopupMenu()

    val folder = psiElement.containingFile.parent?.virtualFile?.path

    val filenameWithoutExtension =
        psiElement.containingFile.name.removeSuffix(".dart")

    val elementVirtualFile = psiElement.containingFile.virtualFile

    return JPopupMenu("build runner build").apply {

        val dartProjectName = PubspecYamlUtil.findPubspecYamlFile(
            psiElement.project,
            elementVirtualFile
        )?.let { PubspecYamlUtil.getDartProjectName(it) }

        val header =
            JMenuItem(
                "<html><small>package: ${dartProjectName}</small></html>",
                DartIcons.Dart_16
            )

        add(header.also { isEnabled = false })

        buildRunnerAnnotation.filePatterns.map {
            val outputFilename = it.replace("*", filenameWithoutExtension)
            val generatedFilename = folder + File.separator + outputFilename

            val item = JMenuItem("Generate $outputFilename")

            item.addActionListener {
                buildRunnerBuild(
                    project = psiElement.project,
                    virtualFile = elementVirtualFile,
                    buildFilter = listOf(generatedFilename),
                    deleteConflictingOutputs = false
                )
            }

            add(item)
        }

        add(JSeparator())

        val generateAll = JMenuItem(
            "Generate All (delete conflicting outputs)"
        ).apply {
            addActionListener {
                buildRunnerBuild(
                    project = psiElement.project,
                    virtualFile = elementVirtualFile,
                    deleteConflictingOutputs = true
                )
            }
        }

        add(
            generateAll
        )
    }
}