@file:Suppress("KotlinUnreachableCode")

package com.github.frankfuenmayor.flutterhelper.buildrunner.codeInsight

import com.github.frankfuenmayor.flutterhelper.buildrunner.settings.BuildRunnerBuildKnownAnnotations
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.psi.PsiElement
import com.intellij.util.ui.JBUI
import com.jetbrains.lang.dart.util.PubspecYamlUtil
import icons.DartIcons
import java.io.File
import javax.swing.JLabel
import javax.swing.ListCellRenderer
import javax.swing.border.EmptyBorder

class CreateBuildRunnerPopupMenu(
    private val knownAnnotations: BuildRunnerBuildKnownAnnotations = BuildRunnerBuildKnownAnnotations(),
) {
    operator fun invoke(
        psiElement: PsiElement,
        onBuild: (deleteConflictingOutputs: Boolean, List<String>) -> Unit
    ): JBPopup? {

        val annotationIdentifier = psiElement.nextSibling.text

        val buildRunnerAnnotation =
            knownAnnotations.findAnnotation(annotationIdentifier) ?: return null

        val folder = psiElement.containingFile.parent?.virtualFile?.path

        val filenameWithoutExtension =
            psiElement.containingFile.name.removeSuffix(".dart")

        val elementVirtualFile = psiElement.containingFile.virtualFile

        val items = mutableListOf<String>()

        val filters = buildRunnerAnnotation.filePatterns.map {
            val outputFilename = it.replace("*", filenameWithoutExtension)
            outputFilename to folder + File.separator + outputFilename
        }

        if (filters.isNotEmpty()) {
            filters.forEach {
                val outputFilename = it.first
                items.add("Generate '$outputFilename'")
            }
        }

        val dartProjectName = PubspecYamlUtil.findPubspecYamlFile(
            psiElement.project,
            elementVirtualFile
        )?.let { PubspecYamlUtil.getDartProjectName(it) }

        if(filters.size > 1){
            items.add("Generate all file(s) in '$dartProjectName'")
        }

        items.add("Generate all file(s) in '$dartProjectName' (delete conflicting outputs)")

        return JBPopupFactory.getInstance().createPopupChooserBuilder(items)
            .setItemChosenCallback { item ->
                val index = items.indexOf(item)

                if (index < filters.size) {
                    onBuild(false, listOf(filters[index].second))
                } else {
                    val deleteConflictingOutputs = index == items.size - 1
                    val buildFilter = if (deleteConflictingOutputs) emptyList() else filters.map { it.second }
                    onBuild(deleteConflictingOutputs, buildFilter)
                }


            }.setRenderer(ListCellRenderer { list, value, index, isSelected, cellHasFocus ->
                @Suppress("UseDPIAwareBorders")
                val paddingBorder: EmptyBorder = JBUI.Borders.empty(4, 4)
                JLabel(
                    value,
                    DartIcons.Dart_file,
                    JLabel.LEFT
                ).apply {
                    border = paddingBorder
                }
            })
            .setTitle("Build Runner Build")
            .createPopup()
    }
}