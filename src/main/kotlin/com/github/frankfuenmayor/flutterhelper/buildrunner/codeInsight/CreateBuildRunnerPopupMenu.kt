@file:Suppress("KotlinUnreachableCode")

package com.github.frankfuenmayor.flutterhelper.buildrunner.codeInsight

import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerAnnotation
import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerData
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.psi.PsiElement
import com.intellij.util.ui.JBUI
import icons.DartIcons
import javax.swing.JLabel
import javax.swing.ListCellRenderer
import javax.swing.border.EmptyBorder

class CreateBuildRunnerPopupMenu() {
    operator fun invoke(
        psiElement: PsiElement,
        buildRunnerAnnotation: BuildRunnerAnnotation,
        onBuild: (deleteConflictingOutputs: Boolean, List<String>) -> Unit
    ): JBPopup? {

        val data = BuildRunnerData(psiElement, buildRunnerAnnotation)
        val items = mutableListOf<String>()

        if (data.generateFiles.isNotEmpty()) {
            data.generateFiles.forEach {
                items.add("Generate '${it.name}'")
            }
        }

        if (data.generateFiles.size > 1) {
            items.add("Generate all file(s) in '${data.dartProjectName}'")
        }

        items.add("Generate all file(s) in '${data.dartProjectName}' (--delete-conflicting-outputs)")

        return JBPopupFactory.getInstance().createPopupChooserBuilder(items)
            .setItemChosenCallback { item ->
                val index = items.indexOf(item)

                if (index < data.generateFiles.size) {
                    onBuild(false, listOf(data.generateFiles[index].absolutePath))
                } else {
                    val deleteConflictingOutputs = index == items.size - 1
                    val buildFilter = if (deleteConflictingOutputs) emptyList() else data.generateFiles.map { it.absolutePath }
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
            .setTitle("Build Runner")
            .createPopup()
    }
}
