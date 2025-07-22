package com.github.frankfuenmayor.flutterhelper.buildrunner.options

import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerAnnotation
import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerAnnotation.Companion.builtIns
import com.github.frankfuenmayor.flutterhelper.buildrunner.settings.BuildRunnerAnnotationsService
import com.intellij.openapi.options.Configurable
import com.intellij.ui.AnActionButton
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import org.jetbrains.annotations.Nls
import javax.swing.JComponent
import javax.swing.event.TableModelEvent

class BuildRunnerBuildConfigurable(
    private val buildRunnerAnnotationsService: BuildRunnerAnnotationsService = BuildRunnerAnnotationsService.getInstance()
) : Configurable {
    private var isModified = false
    private lateinit var currentAnnotations: List<BuildRunnerAnnotation>
    private lateinit var tableView: TableView<BuildRunnerAnnotation>

    val model: ListTableModel<BuildRunnerAnnotation> = ListTableModel<BuildRunnerAnnotation>(
        AnnotationColumn,
        FilePatternsColumn
    )

    @Nls
    override fun getDisplayName(): String {
        return "build_runner for Dart settings"
    }

    override fun createComponent(): JComponent? {

        currentAnnotations = BuildRunnerAnnotationsService.getInstance().getAnnotations()
        model.addRows(currentAnnotations.toMutableList())
        tableView = TableView(model)

        model.addTableModelListener { isModified = it.type == TableModelEvent.UPDATE }

        return ToolbarDecorator
            .createDecorator(tableView)
            .disableDownAction()
            .disableUpDownActions()
            .setRemoveActionUpdater { !tableView.items[tableView.selectedRow].isBuiltIn }
            .setRemoveAction { button: AnActionButton? ->
                val selectedRow = tableView.selectedRow
                if (selectedRow >= 0) {
                    val buildRunnerAnnotation = model.getItem(selectedRow)
                    if (!builtIns.contains(buildRunnerAnnotation)) {
                        model.removeRow(selectedRow)
                        isModified = true
                    }
                }
            }
            .setAddAction { button: AnActionButton? ->
                model.addRow(BuildRunnerAnnotation())
                isModified = true
            }
            .setEditActionUpdater {
                !tableView.items[tableView.selectedRow].isBuiltIn
            }
            .createPanel()
    }

    override fun isModified(): Boolean = isModified

    override fun apply() {
        val updated = model.items.filter { it.isValid }
        buildRunnerAnnotationsService.setAnnotations(updated)
        currentAnnotations = model.items.toList()
        isModified = false
    }

    override fun reset() {
        model.items = currentAnnotations.toMutableList()
        model.fireTableDataChanged()
        isModified = false
    }
}

object AnnotationColumn : ColumnInfo<BuildRunnerAnnotation, String>("Annotation") {
    override fun valueOf(item: BuildRunnerAnnotation): String {
        return item.identifier
    }

    override fun isCellEditable(item: BuildRunnerAnnotation): Boolean {

        return builtIns.none { it == item }
    }

    override fun setValue(item: BuildRunnerAnnotation, value: String) {
        item.identifier = value
    }

    override fun getColumnClass(): Class<String> {
        return String::class.java
    }
}

object FilePatternsColumn : ColumnInfo<BuildRunnerAnnotation, String>("Output File Patterns (Comma separated)") {
    override fun valueOf(item: BuildRunnerAnnotation): String? {
        return item.filePatterns.joinToString(", ")
    }

    override fun isCellEditable(item: BuildRunnerAnnotation): Boolean {
        return builtIns.none { it == item }
    }

    override fun setValue(item: BuildRunnerAnnotation, value: String) {
        item.filePatterns = value.split(",")
    }

    override fun getColumnClass(): Class<*> {
        return String::class.java
    }
}