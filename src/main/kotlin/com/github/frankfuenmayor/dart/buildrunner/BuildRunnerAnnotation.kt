package com.github.frankfuenmayor.dart.buildrunner

class BuildRunnerAnnotation(
    identifier: String = "",
    var filePatterns: List<String> = emptyList(),
    var partStatementRequired: Boolean = true
) {

    var identifier = identifier.removePrefix("@")
        set(value) {
            field = value.trim().removePrefix("@")
        }

    val isValid: Boolean
        get() = identifier.isNotBlank() && filePatterns.isNotEmpty()

    val isBuiltIn: Boolean
        get() = builtIns.any { it == this }

    fun getFileNames(baseFileName: String): List<String> = filePatterns.map { it.replace("*", baseFileName) }


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

    override fun toString(): String {
        return "BuildRunnerAnnotation(filePatterns=$filePatterns, partStatementRequired=$partStatementRequired, identifier='$identifier', isValid=$isValid, isBuiltIn=$isBuiltIn)"
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
    filePatterns = listOf("*.mocks.dart"),
    partStatementRequired = false
)

private val mockitoGenerateNiceMocks = BuildRunnerAnnotation(
    identifier = "GenerateNiceMocks",
    filePatterns = listOf("*.mocks.dart"),
    partStatementRequired = false
)