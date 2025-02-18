package com.github.frankfuenmayor.flutterhelper.codeInsight

import com.github.frankfuenmayor.flutterhelper.settings.BuildRunnerBuildKnownAnnotations
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.jetbrains.lang.dart.util.PubspecYamlUtil
import icons.DartIcons
import java.io.File
import javax.swing.JMenuItem
import javax.swing.JPopupMenu
import javax.swing.JSeparator

class CreateBuildRunnerPopupMenu(
    private val knownAnnotations: BuildRunnerBuildKnownAnnotations = BuildRunnerBuildKnownAnnotations(),
) {
    operator fun invoke(
        psiElement: PsiElement,
        onBuild: (deleteConflictingOutputs: Boolean, List<String>) -> Unit
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
                    "<html><small>package: ${dartProjectName}</small></html>"
                ).apply { isEnabled = false }

            add(header)

            val filter = buildRunnerAnnotation.filePatterns.map {
                val outputFilename = it.replace("*", filenameWithoutExtension)
                outputFilename to folder + File.separator + outputFilename
            }

            val allFilesItem = JMenuItem("Generate File${if (filter.size > 1) "(s)" else ""}", DartIcons.Dart_file)

            allFilesItem.addActionListener {
                onBuild(false, filter.map { it.second })
            }

            add(allFilesItem)
            add(JSeparator())

            if (filter.size > 1) {
                filter.forEach {
                    val outputFilename = it.first
                    val generatedFilename = it.second

                    val item = JMenuItem("Generate $outputFilename", DartIcons.Dart_file)
                    item.addActionListener {
                        onBuild(false, listOf(generatedFilename))
                    }
                    add(item)
                }
                add(JSeparator())
            }

            val generateAll = JMenuItem(
                "Generate all files in $dartProjectName (delete conflicting outputs)",
                AllIcons.Actions.GeneratedFolder
            ).apply {
                addActionListener {
                    onBuild(true, emptyList())
                }
            }

            add(
                generateAll
            )
        }
    }
}