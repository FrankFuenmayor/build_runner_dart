package com.github.frankfuenmayor.flutterhelper

import com.github.frankfuenmayor.flutterhelper.buildrunner.RunBuilderRunnerLineMarkerProvider
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.psi.PsiElement
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.jetbrains.lang.dart.DartFileType
import io.mockk.mockk
import io.mockk.verify

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class MyPluginTest : BasePlatformTestCase() {

    //    fun testXMLFile() {
//        val psiFile = myFixture.configureByText(XmlFileType.INSTANCE, "<foo>bar</foo>")
//        val xmlFile = assertInstanceOf(psiFile, XmlFile::class.java)
//
//        assertFalse(PsiErrorElementUtil.hasErrors(project, xmlFile.virtualFile))
//
//        assertNotNull(xmlFile.rootTag)
//
//        xmlFile.rootTag?.let {
//            assertEquals("foo", it.name)
//            assertEquals("bar", it.value.text)
//        }
//    }
//
//    fun testRename() {
//        myFixture.testRename("foo.xml", "foo_after.xml", "a2")
//    }
//
//    fun testProjectService() {
//        val projectService = project.service<MyProjectService>()
//
//        assertNotSame(projectService.getRandomNumber(), projectService.getRandomNumber())
//    }
//
//    override fun getTestDataPath() = "src/test/testData/rename"
    fun `test add line marker to known annotation`() {

        val psiFile = myFixture.configureByText(
            DartFileType.INSTANCE, """                
@freezed //line marker here
void someFunction() {}
""".trimMargin()
        )
        val element = psiFile.findElementAt(psiFile.text.indexOf("@freezed"))!!
        val lineMarker =
            RunBuilderRunnerLineMarkerProvider().getLineMarkerInfo(element)

        assertNotNull(
            "Line marker for annotation ${element.nextSibling.text} should not be null",
            lineMarker
        )
    }

    fun `test 2`() {


        val psiFile = myFixture.configureByText(
            DartFileType.INSTANCE,
            //language=dart
            """                
@freezed //line marker here
void someFunction() {}
""".trimMargin()
        )

        val element = psiFile.findElementAt(psiFile.text.indexOf("@freezed"))!!
        val navigationHandler = mockk<GutterIconNavigationHandler<PsiElement>>()

        @Suppress("UNCHECKED_CAST")
        val lineMarker: LineMarkerInfo<PsiElement> =
            RunBuilderRunnerLineMarkerProvider(
                navigationHandler = navigationHandler
            ).getLineMarkerInfo(element) as LineMarkerInfo<PsiElement>

        lineMarker.navigationHandler.navigate(mockk(), element)

        verify { navigationHandler.navigate(any(), element) }
    }
}


