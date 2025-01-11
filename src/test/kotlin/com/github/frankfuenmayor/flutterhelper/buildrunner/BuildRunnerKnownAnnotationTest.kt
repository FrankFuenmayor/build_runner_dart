package com.github.frankfuenmayor.flutterhelper.buildrunner

import org.junit.Test


class BuildRunnerKnownAnnotationTest {

    @Test
    fun testEquals() {
        val annotation = BuildRunnerKnownAnnotation(
            identifier = "freezed",
            filePatterns = listOf(".dart"),
        )

        val annotation2 = BuildRunnerKnownAnnotation(
            "@freezed",
            filePatterns = listOf(".dart"),
        )

        assert(annotation == annotation2)
    }
}