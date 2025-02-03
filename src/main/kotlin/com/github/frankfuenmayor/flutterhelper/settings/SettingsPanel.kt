package com.github.frankfuenmayor.flutterhelper.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.ProjectManager
import java.awt.BorderLayout
import javax.swing.*


class SettingsPanel : Configurable {
    private val settings =
        SettingsService.getInstance().state

    override fun createComponent(): JComponent = JPanel().apply {

        layout = BorderLayout()

        add(JLabel("Dart build_runner annotations"), BorderLayout.NORTH)
        val model = AnnotationsTableModel(settings);
        val table = JTable(model)
        add(JScrollPane(table), BorderLayout.CENTER)
    }

    override fun isModified(): Boolean {
        return false
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