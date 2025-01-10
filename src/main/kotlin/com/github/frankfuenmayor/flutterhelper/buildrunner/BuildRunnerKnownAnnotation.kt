package com.github.frankfuenmayor.flutterhelper.buildrunner


data class BuildRunnerKnownAnnotation(val identifier: String, var filePatterns: List<String>)
{

    companion object {
        @JvmStatic
        val builtInAnnotations = listOf(
            jsonAnnotation,
            freezedAnnotation,
            unfreezedAnnotation,
        )
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