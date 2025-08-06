package com.github.frankfuenmayor.dart.buildrunner.codeInspection

import com.github.frankfuenmayor.dart.buildrunner.psi.importStatements
import com.github.frankfuenmayor.dart.buildrunner.psi.partStatements
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.jetbrains.lang.dart.util.DartElementGenerator

class AddPartStatementLocalQuickFix(
    private val missingPart: String
) : LocalQuickFix {

    override fun getName(): String = "Add part '$missingPart'"

    override fun getFamilyName(): String = "Add part statements"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val file = descriptor.psiElement.containingFile

        val insertReference = file.partStatements().lastOrNull()
            ?: file.importStatements().lastOrNull()
            ?: file.firstChild

        val partElement = DartElementGenerator
            .createDummyFile(project, "part '$missingPart';").firstChild!!

        file.addAfter(partElement, insertReference)
    }
}
