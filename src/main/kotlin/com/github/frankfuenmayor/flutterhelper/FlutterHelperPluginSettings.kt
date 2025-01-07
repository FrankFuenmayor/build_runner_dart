package com.github.frankfuenmayor.flutterhelper

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import javax.swing.*
import javax.swing.table.AbstractTableModel
import javax.swing.table.DefaultTableModel

data class KnownAnnotation(val name: String, var filePatterns: List<String>)

private val freezedAnnotation = KnownAnnotation("@freezed", listOf(".freezed.dart"))
private val jsonAnnotation = KnownAnnotation("@json", listOf(".g.dart"))

val builtInAnnotations = listOf(
    freezedAnnotation,
    jsonAnnotation,
)

data class FlutterHelperPluginSettings(private var annotations: List<KnownAnnotation> = emptyList()) {
    val buildRunnerKnownAnnotations: List<KnownAnnotation>
        get() = builtInAnnotations + annotations
}

val Project.flutterHelperPluginSettings
    get(): FlutterHelperPluginSettings =
        FlutterHelperPluginSettingsService.getInstance(this).state

@State(
    name = "FlutterHelperPluginSettings",
    storages = [Storage("FlutterHelperPluginSettings.xml")]
)
@Service(Service.Level.PROJECT)
class FlutterHelperPluginSettingsService :
    PersistentStateComponent<FlutterHelperPluginSettings> {
    private var settings = FlutterHelperPluginSettings()

    override fun getState(): FlutterHelperPluginSettings = settings

    override fun loadState(state: FlutterHelperPluginSettings) {
        settings = state
    }

    companion object {
        fun getInstance(project: Project): FlutterHelperPluginSettingsService =
            project.getService(FlutterHelperPluginSettingsService::class.java)
    }
}


class PluginSettingsConfigurable : Configurable {
    private val settings =
        FlutterHelperPluginSettingsService.getInstance(ProjectManager.getInstance().defaultProject).state

    override fun createComponent(): JComponent = JPanel().apply {

        add(JLabel("Dart build_runner annotations"))
        val model = AnnotationsTableModel(settings);
        add(JScrollPane(JTable(model)))
    }

    override fun isModified(): Boolean {
        return true
    }

    override fun apply() {
//        val settings = PluginSettingsService.getInstance(com.intellij.openapi.project.ProjectManager.getInstance().defaultProject).state
//        settings.option1 = option1Field?.text ?: ""
//        settings.option2 = option2Checkbox?.isSelected ?: false
    }

    override fun reset() {
//        val settings = PluginSettingsService.getInstance(com.intellij.openapi.project.ProjectManager.getInstance().defaultProject).state
//        option1Field?.text = settings.option1
//        option2Checkbox?.isSelected = settings.option2
    }

    override fun getDisplayName(): String = "Flutter Helper"
}

class AnnotationsTableModel(private val settings: FlutterHelperPluginSettings) :
    AbstractTableModel() {
    override fun getRowCount(): Int = settings.buildRunnerKnownAnnotations.size + 1

    override fun getColumnCount(): Int = 2

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {

        if (rowIndex >= builtInAnnotations.size) {
            return ""
        }

        val annotation = settings.buildRunnerKnownAnnotations[rowIndex]

        return when (columnIndex) {
            0 -> annotation.name
            1 -> annotation.filePatterns.joinToString(", ")
            else -> ""
        }
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return rowIndex >= builtInAnnotations.size
    }

    override fun getColumnName(column: Int): String {
        return when (column) {
            0 -> "Annotation"
            1 -> "File Patterns"
            else -> ""
        }
    }
}

