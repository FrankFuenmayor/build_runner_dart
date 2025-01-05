package com.github.frankfuenmayor.flutterhelper

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import javax.swing.*
import javax.swing.table.DefaultTableModel

data class FlutterHelperPluginSettings(
    var buildRunnerKnownAnnotations: List<String> = listOf("freezed")
)

val Project.flutterHelperPluginSettings
    get(): FlutterHelperPluginSettings {
        return FlutterHelperPluginSettingsService.getInstance(this).state
    }

@State(name = "FlutterHelperPluginSettings", storages = [Storage("FlutterHelperPluginSettings.xml")])
@Service(Service.Level.PROJECT)
class FlutterHelperPluginSettingsService : PersistentStateComponent<FlutterHelperPluginSettings> {
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
    private var panel: JPanel? = null

    override fun createComponent(): JComponent = JPanel().apply {

        add(JLabel("Dart build_runner annotations"))

        val model = DefaultTableModel(0, 10)
        model.setColumnIdentifiers(arrayOf("Annotation", "Description"))
        model.addRow(arrayOf("@freezed", "Generate freezed classes"))
        model.addRow(arrayOf("", ""))

        add(JScrollPane(JTable(model)))



    }

    override fun isModified(): Boolean {
        val settings =
            FlutterHelperPluginSettingsService.getInstance(com.intellij.openapi.project.ProjectManager.getInstance().defaultProject).state
        return false;
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

