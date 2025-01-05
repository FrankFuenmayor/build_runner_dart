package com.github.frankfuenmayor.flutterhelper

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.execution.util.ExecUtil
import com.intellij.icons.ExpUiIcons.Run
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task.Backgroundable
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.ui.content.ContentFactory
import com.jetbrains.lang.dart.DartFileType
import com.jetbrains.lang.dart.DartTokenTypes.AT
import com.jetbrains.lang.dart.sdk.DartSdkUtil
import com.jetbrains.lang.dart.util.PubspecYamlUtil
import io.flutter.dart.DartPlugin
import io.flutter.sdk.FlutterSdk
import java.io.File
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class RunBuilderLineMarkerProvider(
    private val knownAnnotations: List<String>? = null
) : LineMarkerProvider {
    override fun getLineMarkerInfo(psiElement: PsiElement): LineMarkerInfo<*>? {

        val isDartFile = psiElement.containingFile.virtualFile.fileType == DartFileType.INSTANCE;

        if (!isDartFile) {
            return null
        }

        if (psiElement.elementType != AT) {
            return null
        }

        val annotationIdentifier = psiElement.nextSibling.text

        val unknownAnnotation =
            (knownAnnotations
                ?: psiElement.project.flutterHelperPluginSettings.buildRunnerAnnotations).none { it == annotationIdentifier }

        if (unknownAnnotation) {
            return null
        }

        return LineMarkerInfo(
            psiElement,
            psiElement.textRange,
            Run.Run,
            { "dart pub run build_runner build" }, { _, _ ->

                val toolWindow =
                    ToolWindowManager.getInstance(psiElement.project).getToolWindow("DartBuildRunnerOutput")

                toolWindow?.show()


                object : Backgroundable(psiElement.project, "Running build_runner run") {
                    override fun run(p0: ProgressIndicator) {
                        kotlin.runCatching {
//                            val dartSdkPath =
//                                DartPlugin.getDartSdk(psiElement.project)?.homePath
//                                    ?: FlutterSdk.getFlutterSdk(psiElement.project)?.dartSdkPath
//                                    ?: return
//
//                            val dartExePath = DartSdkUtil.getDartExePath(dartSdkPath)
//
//                            val yamlFile =
//                                PubspecYamlUtil.findPubspecYamlFile(
//                                    psiElement.project,
//                                    psiElement.containingFile.virtualFile
//                                )
//                                    ?: return
//                            val generalCommandLine = GeneralCommandLine(dartExePath, "run", "build_runner", "build")
//
//                            generalCommandLine.charset = Charsets.UTF_8
//                            generalCommandLine.workDirectory = File(yamlFile.parent.path)


//                            val consoleView = ShellScriptToolWindowManager.getConsoleView(project)
//
//                            consoleView?.print(
//                                "Executing: ${generalCommandLine.preparedCommandLine}\n",
//                                ConsoleViewContentType.SYSTEM_OUTPUT
//                            )


                        }.onFailure {
                            it.printStackTrace()
                        }

                    }

                }.queue()


            }, GutterIconRenderer.Alignment.LEFT, { "" }
        )
    }

    private val Project.flutterHelperPluginSettings
        get(): FlutterHelperPluginSettings {
            return FlutterHelperPluginSettingsService.getInstance(this).state
        }
}

data class FlutterHelperPluginSettings(
    var buildRunnerAnnotations: List<String> = listOf("freezed")
)


@State(name = "FlutterHelperPluginSettings", storages = [Storage("FlutterHelperPluginSettings.xml")])
@Service(Service.Level.PROJECT)
class FlutterHelperPluginSettingsService : PersistentStateComponent<FlutterHelperPluginSettings> {
    private var settings = FlutterHelperPluginSettings()

    override fun getState(): FlutterHelperPluginSettings = settings

    override fun loadState(state: FlutterHelperPluginSettings) {
        settings = state
    }

    companion object {
        fun getInstance(project: com.intellij.openapi.project.Project): FlutterHelperPluginSettingsService =
            project.getService(FlutterHelperPluginSettingsService::class.java)
    }
}


class PluginSettingsConfigurable : Configurable {
    private var panel: JPanel? = null
    private var option1Field: JTextField? = null
    private var option2Checkbox: JCheckBox? = null

    override fun createComponent(): JComponent {
        panel = JPanel().apply {
            layout = java.awt.GridLayout(2, 2)
            add(javax.swing.JLabel("Option 1:"))
            option1Field = JTextField()
            add(option1Field)
            add(javax.swing.JLabel("Option 2:"))
            option2Checkbox = JCheckBox()
            add(option2Checkbox)
        }
        return panel!!
    }

    override fun isModified(): Boolean {
        val settings =
            FlutterHelperPluginSettingsService.getInstance(com.intellij.openapi.project.ProjectManager.getInstance().defaultProject).state
        return option1Field?.text != settings.buildRunnerAnnotations.joinToString(",")
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

    override fun getDisplayName(): String = "My Plugin Settings"
}


class ShellScriptToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val consoleView = ConsoleViewImpl(project, true)
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(consoleView.component, "", false)
        toolWindow.contentManager.addContent(content)

        // Example: Add initial text to the console
        consoleView.print("Shell Script Output:\n", ConsoleViewContentType.NORMAL_OUTPUT)

//        ShellScriptToolWindowManager.register(project, consoleView)
    }
}

//object ShellScriptToolWindowManager {
//    private val consoleViews = mutableMapOf<Project, ConsoleView>()
//
//    fun register(project: Project, consoleView: ConsoleView) {
//        consoleViews[project] = consoleView
//    }
//
//    fun getConsoleView(project: Project): ConsoleView? = consoleViews[project]
//}