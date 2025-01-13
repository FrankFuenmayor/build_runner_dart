package com.github.frankfuenmayor.flutterhelper.buildrunner

import org.junit.Test


class AnnotationTest {

    @Test
    fun testEquals() {
        val annotation = Annotation(
            identifier = "freezed",
            filePatterns = listOf(".dart"),
        )

        val annotation2 = Annotation(
            "@freezed",
            filePatterns = listOf(".dart"),
        )

        assert(annotation == annotation2)
    }
}