package com.github.frankfuenmayor.dart.buildrunner

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.jetbrains.lang.dart.util.PubspecYamlUtil
import org.yaml.snakeyaml.Yaml
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


        val virtualFile = (psiElement as? PsiDirectory)?.virtualFile ?: psiElement!!.containingFile.virtualFile
        val pubspecYamlFile = findPubspecYamlFile(psiElement!!.project, virtualFile)
        val projectName = pubspecYamlFile?.let { getDartProjectName(it) }
        val projectFolder = pubspecYamlFile?.let { File(it.parent.path) }
        val outputFiles = getOutputFiles()

        val missingBuildRunnerDependency = isMissingBuildRunnerDependency(pubspecYamlFile)

        return BuildRunnerData(
            filename = virtualFile.name,
            dartProjectName = projectName ?: throw RuntimeException("Project name not found"),
            projectFolder = projectFolder ?: throw RuntimeException("Project folder not found"),
            outputFiles = outputFiles,
            project = psiElement!!.project,
            missingBuildRunnerDependency = missingBuildRunnerDependency,
        )
    }

    private fun getOutputFiles(): List<File> {

        if( buildRunnerAnnotation == null) {
            return emptyList()
        }

        val containingFile = psiElement!!.containingFile
        val fileFolder = containingFile.virtualFile.parent.path
        val baseFilename = containingFile.name.removeSuffix(".dart")
        val ignoredParts = containingFile.ignoreBuildRunnerAnnotationsMissingParts()

        return buildRunnerAnnotation!!.filePatterns
            .map { it.replace("*", baseFilename) }
            .filterNot { ignoredParts.contains(it) }
            .map { Paths.get(fileFolder, it).toFile() }
    }

    private fun isMissingBuildRunnerDependency(pubspecYamlFile: VirtualFile?): Boolean {
        val pubspecYaml = Yaml().loadAs(pubspecYamlFile?.inputStream, Map::class.java)

        @Suppress("UNCHECKED_CAST")
        val devDependencies = pubspecYaml["dev_dependencies"] as? Map<String, String>

        val missingBuildRunnerDependency = devDependencies?.containsKey("build_runner") == false
        return missingBuildRunnerDependency
    }
}

 data class BuildRunnerData(
    val dartProjectName: String,
    val filename: String,
    val outputFiles: List<File>,
    val project: Project,
    val projectFolder: File,
    val missingBuildRunnerDependency: Boolean,
)

private fun PsiFile.ignoreBuildRunnerAnnotationsMissingParts(): List<String> = text
    .split("\n")
    .map { it.trim() }
    .filter { it.startsWith("// ignore_missing_part:") }
    .map { it.removePrefix("// ignore_missing_part:").trim() }