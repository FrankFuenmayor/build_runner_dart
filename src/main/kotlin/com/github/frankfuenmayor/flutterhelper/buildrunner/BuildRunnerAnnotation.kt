package com.github.frankfuenmayor.flutterhelper.buildrunner


class BuildRunnerAnnotationBuilder {
    private var identifier = ""
    private var filePatterns = emptyList<String>()

    fun identifier(identifier: String) = apply {
        this.identifier = identifier
    }

    fun filePatterns(filePatterns: List<String>) = apply {
        this.filePatterns = filePatterns
    }

    fun build() = BuildRunnerAnnotation(identifier, filePatterns)
}


class BuildRunnerAnnotation(identifier: String, var filePatterns: List<String> = emptyList()) {

    private var _identifier = identifier.removePrefix("@")
    var identifier: String
        get() = _identifier.removePrefix("@")
        set(value) {
            _identifier = value.removePrefix("@")
        }

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
        return identifier == other.identifier
    }

    override fun hashCode(): Int {
        return identifier.hashCode()
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