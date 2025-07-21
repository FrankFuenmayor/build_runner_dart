package com.github.frankfuenmayor.flutterhelper.buildrunner

import com.intellij.psi.PsiElement
import com.jetbrains.lang.dart.util.PubspecYamlUtil
import java.nio.file.Paths

class BuildRunnerData(psiElement: PsiElement, buildRunnerAnnotation: BuildRunnerAnnotation) {
    private val pubspecYamlFile by lazy {
        PubspecYamlUtil.findPubspecYamlFile(
            psiElement.project,
            psiElement.containingFile.virtualFile
        ) ?: throw RuntimeException("Pubspec.yaml not found")
    }

    private val folder by lazy {
        psiElement.containingFile.parent?.virtualFile?.path ?: throw RuntimeException("Folder not found")
    }

    private val filenameWithoutExtension by lazy {
        psiElement.containingFile.name.removeSuffix(".dart")
    }

    val dartProjectName by lazy {
        PubspecYamlUtil.getDartProjectName(pubspecYamlFile) ?: throw RuntimeException("Project name not found")
    }

    val generateFiles by lazy {
        buildRunnerAnnotation.filePatterns.map {
            val outputFilename = it.replace("*", filenameWithoutExtension)
            Paths.get(folder, outputFilename).toFile()
        }
    }
}