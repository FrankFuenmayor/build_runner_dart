package com.github.frankfuenmayor.flutterhelper.settings

import com.android.adblib.utils.toImmutableList
import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerAnnotation
import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerAnnotation.Companion.builtIns
import com.intellij.DynamicBundle
import org.jetbrains.annotations.PropertyKey
import javax.swing.table.AbstractTableModel

class AnnotationsTableModel(val initialAnnotations: List<BuildRunnerAnnotation> = emptyList()) : AbstractTableModel() {

    private var annotations = initialAnnotations.toMutableList()

    override fun getRowCount(): Int = annotations.size

    override fun getColumnCount(): Int = 2

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        val annotation = annotations[rowIndex]

        return when (columnIndex) {
            0 -> annotation.identifier
            1 -> annotation.filePatterns.joinToString(", ")
            else -> throw IllegalArgumentException("Column index $columnIndex is out of bounds")
        }
    }

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        val buildRunnerAnnotation = annotations[rowIndex]

        if (columnIndex == 0) {
            buildRunnerAnnotation.identifier = aValue as String
        } else {
            buildRunnerAnnotation.filePatterns = (aValue as String).split(",")
        }
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = rowIndex >= builtIns.size

    override fun getColumnName(column: Int): String {
        return when (column) {
            0 -> AnnotationsTableModelBundle.message("identifier") //TODO i18n
            1 -> AnnotationsTableModelBundle.message("filePatterns") //TODO i18n
            else -> ""
        }
    }

    fun addRow() {
        annotations.add(BuildRunnerAnnotation("", emptyList()))
        fireTableDataChanged()
    }

    fun getAnnotations(): List<BuildRunnerAnnotation> {
        return annotations.toImmutableList()
    }

    fun reset() {
        annotations = initialAnnotations.toMutableList()
        fireTableDataChanged()
    }
}

private const val BUNDLE = "messages.AnnotationsTableModel"


private object AnnotationsTableModelBundle : DynamicBundle(BUNDLE) {

    @JvmStatic
    fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
        getMessage(key, *params)

    @Suppress("unused")
    @JvmStatic
    fun messagePointer(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
        getLazyMessage(key, *params)
}

