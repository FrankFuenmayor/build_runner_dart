package com.github.frankfuenmayor.dart.buildrunner.psi

import com.intellij.psi.PsiFile
import com.intellij.psi.util.elementType
import com.jetbrains.lang.dart.DartTokenTypes
import com.jetbrains.lang.dart.psi.DartImportStatement
import com.jetbrains.lang.dart.psi.DartMetadata
import com.jetbrains.lang.dart.psi.DartPartStatement

fun PsiFile.partStatements() = children.filter {
    it.elementType == DartTokenTypes.PART_STATEMENT
}.map { it as DartPartStatement }

fun PsiFile.importStatements() = children.filter {
    it.elementType == DartTokenTypes.IMPORT_STATEMENT
}.map { it as DartImportStatement }

fun PsiFile.getMetadataInFile(): List<DartMetadata> = children
    .filter {
        it.elementType == DartTokenTypes.CLASS_DEFINITION ||
                it.elementType == DartTokenTypes.FUNCTION_DECLARATION_WITH_BODY_OR_NATIVE
    }
    .flatMap { classOrFunctionDeclaration ->
        classOrFunctionDeclaration.children.filter { it.elementType == DartTokenTypes.METADATA }
    }
    .map { it as DartMetadata }

fun PsiFile.getPartStatementsInFile(): List<DartPartStatement> = this.children
    .filter { it.elementType == DartTokenTypes.PART_STATEMENT }
    .map { it as DartPartStatement }
