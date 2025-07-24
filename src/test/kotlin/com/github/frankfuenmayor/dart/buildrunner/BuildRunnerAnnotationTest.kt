package com.github.frankfuenmayor.dart.buildrunner

import org.junit.Assert.assertEquals
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

        assertEquals(buildRunnerAnnotation, buildRunnerAnnotation2)

    }
}