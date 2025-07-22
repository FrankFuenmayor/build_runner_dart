package com.github.frankfuenmayor.flutterhelper.buildrunner

interface BuildRunnerAnnotations {

    fun getAnnotations(): List<BuildRunnerAnnotation>

    fun setAnnotations(annotation: List<BuildRunnerAnnotation>)

    fun removeAnnotation(annotation: BuildRunnerAnnotation) {
        setAnnotations(getAnnotations() - annotation)
    }

    fun isKnown(annotationIdentifier: String): Boolean {
        return getAnnotations().any { it.identifier == annotationIdentifier }
    }

    fun findAnnotation(annotationIdentifier: String): BuildRunnerAnnotation? {
        return getAnnotations().find { it.identifier == annotationIdentifier }
    }

}