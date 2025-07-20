package com.github.frankfuenmayor.flutterhelper.buildrunner

import com.intellij.ide.IconProvider
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import javax.swing.Icon


object Icons {
    @JvmField
    val Run: Icon = IconLoader.getIcon("/icons/run.svg", Icons::class.java)
    @JvmField
    val RunError: Icon = IconLoader.getIcon("/icons/run_error.svg", Icons::class.java)
    @JvmField
    val Add: Icon = IconLoader.getIcon("/icons/add.svg", Icons::class.java)
}


class MyIconProvider : IconProvider() {
    override fun getIcon(p0: PsiElement, p1: Int): Icon? {
        TODO("Not yet implemented")
    }
}