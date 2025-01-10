package com.github.frankfuenmayor.flutterhelper.settings

import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerKnownAnnotation.Companion.builtInAnnotations
import javax.swing.table.AbstractTableModel

class KnowAnnotationsTableModel(private val settings: FlutterHelperPluginSettings) : AbstractTableModel() {
    override fun getRowCount(): Int = settings.buildRunnerKnownAnnotations.size

    override fun getColumnCount(): Int = 2

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {

        if (rowIndex >= builtInAnnotations.size) {
            return ""
        }

        val annotation = settings.buildRunnerKnownAnnotations[rowIndex]

        return when (columnIndex) {
            0 -> annotation.identifier
            1 -> annotation.filePatterns.joinToString(", ")
            else -> ""
        }
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = rowIndex >= builtInAnnotations.size

    override fun getColumnName(column: Int): String {
        return when (column) {
            0 -> "Annotation" //TODO i18n
            1 -> "File Patterns" //TODO i18n
            else -> ""
        }
    }
}