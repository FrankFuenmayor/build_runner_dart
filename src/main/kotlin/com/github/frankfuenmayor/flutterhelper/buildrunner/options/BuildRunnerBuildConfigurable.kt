package com.github.frankfuenmayor.flutterhelper.buildrunner.options

import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerAnnotation
import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerAnnotation.Companion.builtIns
import com.intellij.openapi.options.Configurable
import com.intellij.ui.AnActionButton
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import org.jetbrains.annotations.Nls
import javax.swing.JComponent
import javax.swing.JPanel

class BuildRunnerBuildConfigurable : Configurable {
    private var mainPanel: JPanel? = null
    val data = builtIns.toMutableList()

    val model: ListTableModel<BuildRunnerAnnotation?> = ListTableModel<BuildRunnerAnnotation?>(
        AnnotationColumn,
        FilePatternsColumn
    )


    @Nls
    override fun getDisplayName(): String {
        return "Dart Build Runner Settings"
    }

    override fun createComponent(): JComponent? {
        model.addRows(data)
        val tableView = TableView<BuildRunnerAnnotation>(model)
        return ToolbarDecorator
            .createDecorator(tableView)
            .disableDownAction()
            .disableUpDownActions()
            .setRemoveAction { button: AnActionButton? ->
                val selectedRow = tableView.selectedRow

                if (selectedRow >= 0) {
                    val buildRunnerAnnotation = model.getItem(selectedRow)
                    if (!builtIns.contains(buildRunnerAnnotation)) {
                        model.removeRow(selectedRow)
                    }
                }
            }
            .setAddAction { button: AnActionButton? ->
                model.addRow(BuildRunnerAnnotation("", mutableListOf<String>()))
            }
            .createPanel()
    }

    override fun isModified(): Boolean {
        // TODO: implement persistence logic
        return false
    }

    override fun apply() {
//        SettingsService
//                .getInstance()
//                .setAnnotations(model.getAnnotations());
    }

    override fun reset() {
        // TODO: reset the table to saved values
    }

    override fun disposeUIResources() {
        mainPanel = null
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

object FilePatternsColumn : ColumnInfo<BuildRunnerAnnotation, String>("File Patterns") {
    override fun valueOf(item: BuildRunnerAnnotation): String? {
        return item.filePatterns.joinToString(", ")
    }

    override fun isCellEditable(item: BuildRunnerAnnotation?): Boolean {
        return builtIns.none { it == item }
    }

    override fun setValue(item: BuildRunnerAnnotation?, value: String?) {

    }

    override fun getColumnClass(): Class<*> {
        return String::class.java
    }
}