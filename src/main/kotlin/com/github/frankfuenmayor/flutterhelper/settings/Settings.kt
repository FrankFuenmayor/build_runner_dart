package com.github.frankfuenmayor.flutterhelper.settings

import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerAnnotation
import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerAnnotation.Companion.builtIns

data class Settings(
    @JvmField val annotations: List<BuildRunnerAnnotation> = builtIns
)


