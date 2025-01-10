package com.github.frankfuenmayor.flutterhelper.buildrunner

import com.github.frankfuenmayor.flutterhelper.settings.flutterHelperPluginSettings
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.ui.ConsoleView
import com.intellij.icons.ExpUiIcons.Run
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.ui.content.ContentFactory
import com.jetbrains.lang.dart.DartFileType
import com.jetbrains.lang.dart.DartTokenTypes.AT

class RunBuilderRunnerLineMarkerProvider(
    private val knownAnnotations: List<BuildRunnerKnownAnnotation>? = null,
    private val navigationHandler: GutterIconNavigationHandler<PsiElement> = RunBuilderRunnerNavigationHandler()
) : LineMarkerProvider {
    override fun getLineMarkerInfo(psiElement: PsiElement): LineMarkerInfo<*>? {

        val isDartFile =
            psiElement.containingFile.virtualFile.fileType == DartFileType.INSTANCE;

        if (!isDartFile) {
            return null
        }

        if (psiElement.elementType != AT) {
            return null
        }

        val annotationIdentifier = psiElement.nextSibling.text

        val unknownAnnotation =
            (knownAnnotations
                ?: psiElement.project.flutterHelperPluginSettings.buildRunnerKnownAnnotations)
                .none { knownAnnotation ->
                    knownAnnotation.identifier.removePrefix("@") == annotationIdentifier.removePrefix("@")
                }

        if (unknownAnnotation) {
            return null
        }

        return LineMarkerInfo(
            psiElement,
            psiElement.textRange,
            Run.Run,
            { "dart pub run build_runner build" },
            navigationHandler,
            GutterIconRenderer.Alignment.LEFT,
            { "" }
        )
    }
}

class DartBuildRunnerOutputWindow : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val consoleView = ConsoleViewImpl(project, true)
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(consoleView.component, "", false)
        toolWindow.contentManager.addContent(content)

        ShellScriptToolWindowManager.register(project, consoleView)
    }
}

object ShellScriptToolWindowManager {
    private val consoleViews = mutableMapOf<Project, ConsoleView>()

    fun register(project: Project, consoleView: ConsoleView) {
        consoleViews[project] = consoleView
    }

    fun getConsoleView(project: Project): ConsoleView? = consoleViews[project]
}