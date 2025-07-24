package com.github.frankfuenmayor.dart.buildrunner

class BuildRunnerAnnotation(identifier: String = "", var filePatterns: List<String> = emptyList()) {

    var identifier = identifier.removePrefix("@")
        set(value) {
            field = value.trim().removePrefix("@")
        }

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BuildRunnerAnnotation

        if (filePatterns != other.filePatterns) return false
        if (identifier != other.identifier) return false

        return true
    }

    override fun hashCode(): Int {
        var result = filePatterns.hashCode()
        result = 31 * result + identifier.hashCode()
        return result
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