package com.github.frankfuenmayor.flutterhelper.settings

import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerAnnotation
import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerAnnotation.Companion.builtIns

class Settings(private var annotations: List<BuildRunnerAnnotation> = emptyList()) {
    val buildRunnerBuildRunnerAnnotations: List<BuildRunnerAnnotation> get() = builtIns + annotations
}


