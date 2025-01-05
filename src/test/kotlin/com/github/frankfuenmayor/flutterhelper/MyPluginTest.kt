package com.github.frankfuenmayor.flutterhelper

import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.openapi.wm.IdeFrame
import com.intellij.psi.PsiElement
import com.intellij.ui.components.Label
import com.jetbrains.lang.dart.DartFileType
import com.jetbrains.lang.dart.psi.DartMetadata
import io.mockk.mockk
import java.awt.event.MouseEvent
import javax.swing.JComponent

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
        val lineMarker = RunBuilderLineMarkerProvider().getLineMarkerInfo(element)

        assertNotNull("Line marker for annotation ${element.nextSibling.text} should not be null", lineMarker)
    }
}


