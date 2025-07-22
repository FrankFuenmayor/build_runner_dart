package com.github.frankfuenmayor.flutterhelper.buildrunner

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.jetbrains.lang.dart.util.PubspecYamlUtil
import java.io.File
import java.nio.file.Paths


class BuildRunnerDataBuilder(
    private val findPubspecYamlFile: (Project, VirtualFile) -> VirtualFile? = PubspecYamlUtil::findPubspecYamlFile,
    private val getDartProjectName: (VirtualFile) -> String? = PubspecYamlUtil::getDartProjectName
) {

    private var psiElement: PsiElement? = null
    private var buildRunnerAnnotation: BuildRunnerAnnotation? = null

    fun setPsiElement(psiElement: PsiElement) = apply {
        this.psiElement = psiElement
    }

    fun setBuildRunnerAnnotation(buildRunnerAnnotation: BuildRunnerAnnotation) = apply {
        this.buildRunnerAnnotation = buildRunnerAnnotation
    }

    fun build(): BuildRunnerData {

        if (psiElement == null) throw RuntimeException("PsiElement not set")
        if (buildRunnerAnnotation == null) throw RuntimeException("BuildRunnerAnnotation not set")

        val containingFile = psiElement!!.containingFile
        val pubspecYamlFile = findPubspecYamlFile(psiElement!!.project, containingFile.virtualFile)
        val projectName = pubspecYamlFile?.let { getDartProjectName(it) }
        val fileFolder = containingFile.virtualFile.parent.path
        val fileName = containingFile.name.removeSuffix(".dart")

        val outputFiles = fileFolder.let { fileFolder ->
            buildRunnerAnnotation!!.filePatterns.map {
                val outputFilename = it.replace("*", fileName)
                Paths.get(fileFolder, outputFilename).toFile()
            }
        }

        val projectFolder = pubspecYamlFile?.let { File(it.parent.path) }

        return BuildRunnerData(
            dartProjectName = projectName ?: throw RuntimeException("Project name not found"),
            projectFolder = projectFolder ?: throw RuntimeException("Project folder not found"),
            outputFiles = outputFiles
        )
    }

}

data class BuildRunnerData(
    val dartProjectName: String,
    val projectFolder: File,
    val outputFiles: List<File>
)
