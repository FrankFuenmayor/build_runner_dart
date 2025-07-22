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

        val pubspecYamlFile = findPubspecYamlFile(
            psiElement!!.project,
            psiElement!!.containingFile.virtualFile
        )

        val projectName = pubspecYamlFile?.let { getDartProjectName(it) }

        val folder = pubspecYamlFile?.let { File(it.parent.path) }
        val fileName = psiElement!!.containingFile.name.removeSuffix(".dart")

        val outputFiles = folder?.let {
            buildRunnerAnnotation!!.filePatterns.map {
                val outputFilename = it.replace("*", fileName)
                Paths.get(it, outputFilename).toFile()
            }
        }


        return BuildRunnerData(
            projectName ?: throw RuntimeException("Project name not found"),
            folder ?: throw RuntimeException("Project folder not found"),
            outputFiles ?: throw RuntimeException("Output files not found")
        )
    }

}

data class BuildRunnerData(
    val dartProjectName: String,
    val projectFolder: File,
    val outputFiles: List<File>
)
