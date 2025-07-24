package com.github.frankfuenmayor.dart.buildrunner.settings

import com.github.frankfuenmayor.dart.buildrunner.BuildRunnerAnnotation
import com.github.frankfuenmayor.dart.buildrunner.BuildRunnerAnnotation.Companion.builtIns

data class Settings(
    @JvmField val annotations: List<BuildRunnerAnnotation> = builtIns
)


