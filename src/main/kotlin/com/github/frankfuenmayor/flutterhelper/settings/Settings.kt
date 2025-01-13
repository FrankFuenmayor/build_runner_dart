package com.github.frankfuenmayor.flutterhelper.settings

import com.github.frankfuenmayor.flutterhelper.buildrunner.Annotation
import com.github.frankfuenmayor.flutterhelper.buildrunner.Annotation.Companion.builtIns
import com.intellij.openapi.project.Project

class Settings(private var annotations: List<Annotation> = emptyList()) {
    val buildRunnerAnnotations: List<Annotation> get() = builtIns + annotations
}

val Project.settings get(): Settings = SettingsService.getInstance(this).state


