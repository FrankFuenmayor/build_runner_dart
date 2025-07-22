package com.github.frankfuenmayor.flutterhelper.codeInsight

import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerData
import com.github.frankfuenmayor.flutterhelper.buildrunner.action.BuildRunnerBuild
import com.github.frankfuenmayor.flutterhelper.buildrunner.codeInsight.CreateBuildRunnerPopupMenu
import com.github.frankfuenmayor.flutterhelper.buildrunner.codeInsight.RunBuilderRunnerNavigationHandler
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.jetbrains.lang.dart.DartFileType
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.intellij.lang.annotations.Language
import java.awt.Component
import java.awt.Point
import java.awt.event.MouseEvent

class RunBuilderRunnerNavigationHandlerTest : BasePlatformTestCase() {

    fun `test - navigate successfully`() {
        val buildRunnerBuild = mockBuildRunnerBuild()
        val createPopupMenu = mockCreateBuildRunnerPopupMenu()
        val mouseEvent = mockMouseEvent()
        val buildDRunnerData = mockk<BuildRunnerData>()

        val navigationHandler = RunBuilderRunnerNavigationHandler(
            buildRunnerData = buildDRunnerData,
            buildRunnerBuild = buildRunnerBuild,
            createPopupMenu = createPopupMenu
        )

        val file = dartFileWithContent(
            """                
            | @freezed
            | void someFunction() {}
            """.trimMargin()
        )

        val annotationElement = file.findElement("@freezed")
        val popupMenu = mockk<JBPopup>(relaxed = true)

        every { createPopupMenu(buildDRunnerData, any()) } returns popupMenu

        navigationHandler.navigate(mouseEvent, annotationElement)

        verify {
            createPopupMenu.invoke(buildDRunnerData, any())
            popupMenu.showInScreenCoordinates(any(), any())
        }
    }

    private fun mockBuildRunnerBuild() = mockk<BuildRunnerBuild>()

    private fun mockCreateBuildRunnerPopupMenu(): CreateBuildRunnerPopupMenu {
        val m = mockk<CreateBuildRunnerPopupMenu>()
        every { m.invoke(any(), any()) } returns mockk()
        return m
    }

    private fun mockMouseEvent(): MouseEvent {
        return mockk<MouseEvent>().apply {
            every { component } returns mockk<Component>().apply {
                every { parent } returns null
                every { locationOnScreen } returns Point()
            }
            every { x } returns 1
            every { y } returns 1
            every { xOnScreen } returns 1
            every { yOnScreen } returns 1
        }
    }

    private fun dartFileWithContent(@Language("dart") code: String): PsiFile =
        myFixture.configureByText(DartFileType.INSTANCE, code)

}