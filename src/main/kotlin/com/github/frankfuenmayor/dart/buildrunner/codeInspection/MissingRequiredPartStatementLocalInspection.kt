package com.github.frankfuenmayor.dart.buildrunner.codeInspection

import com.github.frankfuenmayor.dart.buildrunner.BuildRunnerAnnotation
import com.github.frankfuenmayor.dart.buildrunner.BuildRunnerAnnotations
import com.github.frankfuenmayor.dart.buildrunner.psi.getMetadataInFile
import com.github.frankfuenmayor.dart.buildrunner.psi.getPartStatementsInFile
import com.github.frankfuenmayor.dart.buildrunner.components.BuildRunnerAnnotationsService
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiFile
import com.jetbrains.lang.dart.psi.DartMetadata

class MissingRequiredPartStatementLocalInspection(
    private val buildRunnerAnnotations: BuildRunnerAnnotations = BuildRunnerAnnotationsService.getInstance()
) : LocalInspectionTool() {

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor?>? {

        val metadata = file.getMetadataInFile().takeIf { it.isNotEmpty() }
            ?: return null

        val annotations =
            metadata.mapNotNull { buildRunnerAnnotations.findAnnotation(it.referenceExpression.text) }
                .filter { it.partStatementRequired }
                .takeIf { it.isNotEmpty() }
                ?: return null

        val fileNameWithoutExtension = file.name.removeSuffix(".dart")

        val requiredParts = annotations.flatMap { it.getFileNames(fileNameWithoutExtension) }
            .takeIf { it.isNotEmpty() }
            ?: return null

        val parts = file.getPartStatementsInFile().map { it.uriString }

        val missingPartFiles = requiredParts
            .filterNot { file.text.contains("// ignore_missing_part: $it") }
            .filterNot { parts.contains(it) }
            .takeIf { it.isNotEmpty() }
            ?: return null

        return missingPartFiles
            .map { missingPart ->
                manager.createProblemDescriptor(
                    metadata.findMetadata(annotations),
                    "Missing '$missingPart' part",
                    AddPartStatementLocalQuickFix(missingPart),
                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                    isOnTheFly
                )
            }.toTypedArray()
    }
}

private fun List<DartMetadata>.findMetadata(
    annotations: List<BuildRunnerAnnotation>
): DartMetadata =
    find { metadata -> metadata.referenceExpression.text in annotations.map { annotation -> annotation.identifier } }!!
