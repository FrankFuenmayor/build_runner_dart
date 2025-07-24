@file:Suppress("KotlinUnreachableCode")

package com.github.frankfuenmayor.dart.buildrunner.codeInsight

import com.github.frankfuenmayor.dart.buildrunner.BuildRunnerData
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import icons.DartIcons
import java.io.File
import javax.swing.JLabel
import javax.swing.ListCellRenderer

class CreateBuildRunnerPopupMenu() {
    operator fun invoke(
        buildRunnerData: BuildRunnerData,
        onBuild: (deleteConflictingOutputs: Boolean, outputFiles: List<File>) -> Unit
    ): JBPopup {

        val items = mutableListOf<String>()

        if (buildRunnerData.outputFiles.isNotEmpty()) {
            buildRunnerData.outputFiles.forEach {
                items.add("Generate '${it.name}'")
            }
        }

        if (buildRunnerData.outputFiles.size > 1) {
            items.add("Generate all file(s) in '${buildRunnerData.dartProjectName}'")
        }

        items.add("Generate all file(s) in '${buildRunnerData.dartProjectName}' (--delete-conflicting-outputs)")

        return JBPopupFactory
            .getInstance()
            .createPopupChooserBuilder(items)
            .setItemChosenCallback { item ->
                val index = items.indexOf(item)

                if (index < buildRunnerData.outputFiles.size) {
                    onBuild(false, listOf(buildRunnerData.outputFiles[index]))
                } else {
                    val deleteConflictingOutputs = index == items.size - 1
                    onBuild(deleteConflictingOutputs, buildRunnerData.outputFiles)
                }
            }.setRenderer(ListCellRenderer { list, value, index, isSelected, cellHasFocus ->
                JLabel(
                    value,
                    DartIcons.Dart_file,
                    JLabel.LEFT
                )
            })
            .setTitle("Build Runner")
            .createPopup()
    }
}
