package com.github.frankfuenmayor.flutterhelper.codeInsight

import com.github.frankfuenmayor.flutterhelper.buildrunner.codeInsight.RunBuilderRunnerLineMarkerProvider
import com.github.frankfuenmayor.flutterhelper.buildrunner.settings.BuildRunnerBuildKnownAnnotations
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.jetbrains.lang.dart.DartFileType
import io.mockk.*
import org.intellij.lang.annotations.Language

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class RunBuilderRunnerLineMarkerProviderTest : BasePlatformTestCase() {

    fun `test - add marker to known annotation`() {

        val psiFile = dartFileWithContent(
            """                
            | @freezed
            | void someFunction() {}
            """.trimMargin()
        )

        val annotationElement = psiFile.findElement(FREEZED_ANNOTATION)
        val lineMarkerProvider = newRunBuilderRunnerLineMarkerProvider()
        val expectedLineMarkerInfo =
            lineMarkerProvider.getLineMarkerInfo(annotationElement)

        assertNotNull(
            "Line marker for annotation ${annotationElement.nextSibling.text} should not be null",
            expectedLineMarkerInfo
        )
    }

    fun `test - do not add marker to unknown annotation`() {

        val psiFile = dartFileWithContent(
            """
            | @UnknownAnnotation
            | void someFunction() {} 
            """.trimMargin()
        )

        val annotationElement = psiFile.findElement("@UnknownAnnotation")
        val lineMarkerProvider = newRunBuilderRunnerLineMarkerProvider()
        val expectedNull = lineMarkerProvider.getLineMarkerInfo(annotationElement)

        assertNull(
            "Line marker for annotation ${annotationElement.nextSibling.text} should is null",
            expectedNull
        )
    }

    fun `test - navigate successfully`() {
        val file = dartFileWithContent(
            """                
            | @freezed
            | void someFunction() {}
            """.trimMargin()
        )

        val navigationHandler = newGutterIconNavigationHandler()
            .thatNavigatesSuccessfully()

        val lineMarkerProvider =
            newRunBuilderRunnerLineMarkerProvider(navigationHandler = navigationHandler)

        val annotationElement = file.findElement(FREEZED_ANNOTATION)

        @Suppress("UNCHECKED_CAST")
        val lineMarker: LineMarkerInfo<PsiElement> =
            lineMarkerProvider.getLineMarkerInfo(annotationElement) as LineMarkerInfo<PsiElement>

        lineMarker.navigationHandler.navigate(mockk(), annotationElement)

        verify { navigationHandler.navigate(any(), annotationElement) }
    }

    ///<editor-fold desc="Helper functions">
    private fun newRunBuilderRunnerLineMarkerProvider(
        navigationHandler: GutterIconNavigationHandler<PsiElement> = newGutterIconNavigationHandler()
    ) = RunBuilderRunnerLineMarkerProvider(
        createNavigationHandler = { navigationHandler },
        knownAnnotations = BuildRunnerBuildKnownAnnotations()
    )

    private fun newGutterIconNavigationHandler(): GutterIconNavigationHandler<PsiElement> =
        mockk<GutterIconNavigationHandler<PsiElement>>()

    private fun dartFileWithContent(@Language("dart") code: String): PsiFile =
        myFixture.configureByText(
            DartFileType.INSTANCE,
            //language=dart
            code
        )

    private fun GutterIconNavigationHandler<PsiElement>.thatNavigatesSuccessfully(): GutterIconNavigationHandler<PsiElement> {
        every { navigate(any(), any()) } just Runs
        return this
    }
///</editor-fold>

    companion object {
        private const val FREEZED_ANNOTATION = "@freezed"
    }
}

fun PsiFile.findElement(code: String) = findElementAt(text.indexOf(code))!!
