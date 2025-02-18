package com.github.frankfuenmayor.flutterhelper.codeInsight

import com.github.frankfuenmayor.flutterhelper.buildrunner.action.BuildRunnerBuild
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

    fun `test - TBD`() {
        val buildRunnerBuild = mockk<BuildRunnerBuild>()

        val createPopupMenu = mockk<CreateBuildRunnerPopupMenu>(relaxed = true)

        val navigationHandler = RunBuilderRunnerNavigationHandler(
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
        val mouseEvent = mockMouseEvent()

        navigationHandler.navigate(mouseEvent, annotationElement)

        verify {
            createPopupMenu.invoke(annotationElement, any())
        }
    }

    private fun mockMouseEvent(): MouseEvent {
        return mockk<MouseEvent>().apply {
            every { component } returns mockk<Component>().apply {
                every { parent } returns null
                every { locationOnScreen } returns Point()
            }
            every { x } returns 1
            every { y } returns 1
        }
    }

    private fun dartFileWithContent(@Language("dart") code: String): PsiFile =
        myFixture.configureByText(DartFileType.INSTANCE, code)

}