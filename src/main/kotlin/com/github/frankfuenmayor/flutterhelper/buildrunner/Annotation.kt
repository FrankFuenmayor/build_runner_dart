package com.github.frankfuenmayor.flutterhelper.buildrunner


class Annotation(identifier: String, var filePatterns: List<String>) {

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
        other as Annotation
        return identifier == other.identifier
    }

    override fun hashCode(): Int {
        return identifier.hashCode()
    }
}

private val freezedAnnotation = Annotation(
    identifier = "freezed",
    filePatterns = listOf(".freezed.dart", "g.dart"),
)
private val unfreezedAnnotation = Annotation(
    identifier = "unfreezed",
    filePatterns = listOf(".freezed.dart", "g.dart"),
)
private val jsonAnnotation = Annotation(
    identifier = "JsonSerializable",
    filePatterns = listOf(".g.dart")
)