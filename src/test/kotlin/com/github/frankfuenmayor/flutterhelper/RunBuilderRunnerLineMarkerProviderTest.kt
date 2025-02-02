package com.github.frankfuenmayor.flutterhelper

import com.github.frankfuenmayor.flutterhelper.codeInsight.RunBuilderRunnerLineMarkerProvider
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


    fun `test add line marker to known annotation`() {

        val psiFile = fileWithContent(
            """                
@freezed //line marker here
void someFunction() {}
""".trimMargin()
        )

        val annotationElement = psiFile.findElement(freezedAnnotation)

        val markerForElement =
            RunBuilderRunnerLineMarkerProvider().getLineMarkerInfo(annotationElement)

        assertNotNull(
            "Line marker for annotation ${annotationElement.nextSibling.text} should not be null",
            markerForElement
        )
    }

    fun `test 2`() {
        val code = """                
@freezed //line marker here
void someFunction() {}
""".trimMargin()

        val psiFile = fileWithContent(code)
        val element = psiFile.findElement(freezedAnnotation)

        val navigationHandler = mockk<GutterIconNavigationHandler<PsiElement>>().apply {
            every { navigate(any(), any()) } just Runs
        }

        @Suppress("UNCHECKED_CAST")
        val lineMarker: LineMarkerInfo<PsiElement> =
            RunBuilderRunnerLineMarkerProvider(
                navigationHandler = navigationHandler
            ).getLineMarkerInfo(element) as LineMarkerInfo<PsiElement>

        lineMarker.navigationHandler.navigate(mockk(), element)

        verify { navigationHandler.navigate(any(), element) }
    }

    private fun PsiFile.findElement(code: String) =
        findElementAt(text.indexOf(code))!!

    private fun fileWithContent(@Language("dart") code: String): PsiFile =
        myFixture.configureByText(
            DartFileType.INSTANCE,
            //language=dart
            code
        )
}

private const val freezedAnnotation = "@freezed"

