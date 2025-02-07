package com.github.frankfuenmayor.flutterhelper.codeInsight

import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerAnnotation
import com.github.frankfuenmayor.flutterhelper.buildrunner.action.BuildRunnerBuild
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.mockk.mockk

class RunBuilderRunnerNavigationHandlerTest : BasePlatformTestCase() {

    fun `test - TBD`() {

        val buildRunnerBuild = mockk<BuildRunnerBuild>()

        val navigationHandler = RunBuilderRunnerNavigationHandler(
            buildRunnerAnnotation = BuildRunnerAnnotation(
                identifier = "freezed",
                filePatterns = listOf(".dart"),
            ),
            buildRunnerBuild = buildRunnerBuild
        )

        navigationHandler.navigate(mockk(), mockk())

    }

}