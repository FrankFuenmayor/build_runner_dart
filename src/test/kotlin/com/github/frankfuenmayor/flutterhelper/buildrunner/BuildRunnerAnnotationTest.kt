package com.github.frankfuenmayor.flutterhelper.buildrunner

import org.junit.Test


class BuildRunnerAnnotationTest {

    @Test
    fun testEquals() {
        val buildRunnerAnnotation = BuildRunnerAnnotation(
            identifier = "freezed",
            filePatterns = listOf(".dart"),
        )

        val buildRunnerAnnotation2 = BuildRunnerAnnotation(
            "@freezed",
            filePatterns = listOf(".dart"),
        )

        assert(buildRunnerAnnotation == buildRunnerAnnotation2)
    }
}