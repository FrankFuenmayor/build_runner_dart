package com.github.frankfuenmayor.flutterhelper.buildrunner

data class BuildRunnerAnnotation(var identifier: String = "", var filePatterns: List<String> = emptyList()) {

    val isValid: Boolean
        get() = identifier.isNotBlank() && filePatterns.isNotEmpty()

    val isBuiltIn: Boolean
        get() = builtIns.any { it == this }

    companion object {
        @JvmStatic
        val builtIns = listOf(
            jsonBuildRunnerAnnotation,
            freezedBuildRunnerAnnotation,
            unfreezedBuildRunnerAnnotation,
            mockitoGenerateMocks,
            mockitoGenerateNiceMocks
        )
    }
}

private val freezedBuildRunnerAnnotation = BuildRunnerAnnotation(
    identifier = "freezed",
    filePatterns = listOf("*.freezed.dart", "*.g.dart"),
)

private val unfreezedBuildRunnerAnnotation = BuildRunnerAnnotation(
    identifier = "unfreezed",
    filePatterns = listOf("*.freezed.dart", "*.g.dart"),
)

private val jsonBuildRunnerAnnotation = BuildRunnerAnnotation(
    identifier = "JsonSerializable",
    filePatterns = listOf("*.g.dart")
)

private val mockitoGenerateMocks = BuildRunnerAnnotation(
    identifier = "GenerateMocks",
    filePatterns = listOf("*.mocks.dart")
)

private val mockitoGenerateNiceMocks = BuildRunnerAnnotation(
    identifier = "GenerateNiceMocks",
    filePatterns = listOf("*.mocks.dart")
)