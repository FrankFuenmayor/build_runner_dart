package com.github.frankfuenmayor.flutterhelper.buildrunner

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon


object Icons {
    @JvmField
    val Run: Icon = IconLoader.getIcon("/icons/run.svg", Icons::class.java)
    @JvmField
    val RunError: Icon = IconLoader.getIcon("/icons/run_error.svg", Icons::class.java)
    @JvmField
    val Add: Icon = IconLoader.getIcon("/icons/add.svg", Icons::class.java)
    @JvmField
    val Build: Icon = IconLoader.getIcon("/icons/build.svg", Icons::class.java)
    @JvmField
    val Dart: Icon = IconLoader.getIcon("/icons/dart.svg", Icons::class.java)
}
