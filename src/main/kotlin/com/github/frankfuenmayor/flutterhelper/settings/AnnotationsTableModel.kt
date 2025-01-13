package com.github.frankfuenmayor.flutterhelper.settings

import com.github.frankfuenmayor.flutterhelper.buildrunner.Annotation.Companion.builtIns
import com.intellij.DynamicBundle
import org.jetbrains.annotations.PropertyKey
import javax.swing.table.AbstractTableModel

class AnnotationsTableModel(private val settings: Settings) : AbstractTableModel() {
    override fun getRowCount(): Int = settings.buildRunnerAnnotations.size

    override fun getColumnCount(): Int = 2

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {

        if (rowIndex >= builtIns.size) {
            return ""
        }

        val annotation = settings.buildRunnerAnnotations[rowIndex]

        return when (columnIndex) {
            0 -> annotation.identifier
            1 -> annotation.filePatterns.joinToString(", ")
            else -> ""
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

