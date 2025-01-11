package com.github.frankfuenmayor.flutterhelper.buildrunner


class BuildRunnerKnownAnnotation(identifier: String, var filePatterns: List<String>) {

    val identifier = identifier.removePrefix("@")

    companion object {
        @JvmStatic
        val builtIns = listOf(
            jsonAnnotation,
            freezedAnnotation,
            unfreezedAnnotation,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BuildRunnerKnownAnnotation
        return identifier == other.identifier
    }

    override fun hashCode(): Int {
        return identifier.hashCode()
    }
}

private val freezedAnnotation = BuildRunnerKnownAnnotation(
    identifier = "freezed",
    filePatterns = listOf(".freezed.dart", "g.dart"),
)
private val unfreezedAnnotation = BuildRunnerKnownAnnotation(
    identifier = "unfreezed",
    filePatterns = listOf(".freezed.dart", "g.dart"),
)
private val jsonAnnotation = BuildRunnerKnownAnnotation(
    identifier = "JsonSerializable",
    filePatterns = listOf(".g.dart")
)