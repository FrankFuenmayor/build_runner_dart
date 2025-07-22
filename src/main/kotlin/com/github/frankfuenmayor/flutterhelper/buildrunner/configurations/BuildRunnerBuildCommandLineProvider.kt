package com.github.frankfuenmayor.flutterhelper.buildrunner.configurations

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.jetbrains.lang.dart.sdk.DartSdkUtil
import io.flutter.dart.DartPlugin
import io.flutter.sdk.FlutterSdk
import java.io.File

class BuildRunnerBuildCommandLineProvider(
    val resolveDartExePath: (Project) -> String = ::getDartExePath
) {

    fun getCommandLine(
        project: Project,
        workDirectory: File,
        outputFiles: List<File> = emptyList(),
        deleteConflictingOutputs: Boolean = false
    ): GeneralCommandLine {

        val dartExePath = resolveDartExePath(project)

        val arguments = mutableListOf(
            dartExePath,
            "run",
            "build_runner",
            "build"
        )

        outputFiles.forEach {
            arguments.add("--build-filter")
            arguments.add(it.absolutePath)
        }

        if(deleteConflictingOutputs){
            arguments.add("--delete-conflicting-outputs")
        }

        return GeneralCommandLine(arguments).apply {
            charset = Charsets.UTF_8
            setWorkDirectory(workDirectory)
        }
    }
}

fun getDartExePath(project: Project): String {
    val dartSdkPath = DartPlugin.getDartSdk(project)?.homePath
        ?: FlutterSdk.getFlutterSdk(project)?.dartSdkPath ?: return ""
    return DartSdkUtil.getDartExePath(dartSdkPath)
}