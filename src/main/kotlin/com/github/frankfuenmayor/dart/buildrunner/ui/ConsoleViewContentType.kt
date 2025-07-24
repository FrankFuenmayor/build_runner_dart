package com.github.frankfuenmayor.dart.buildrunner.ui

import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.ui.JBColor

val SUCCESS_OUTPUT = ConsoleViewContentType(
    "SUCCESS_OUTPUT",
    TextAttributesKey.createTextAttributesKey(
        "SUCCESS_OUTPUT"
    ).apply {
        TextAttributes().apply {
            foregroundColor = JBColor.YELLOW
        }
    }
)
