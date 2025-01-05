package com.github.frankfuenmayor.flutterhelper

import com.intellij.openapi.project.Project
import io.flutter.dart.DartPlugin
import io.flutter.sdk.FlutterSdk

val Project.dartSdk: String?
    get() = DartPlugin.getDartSdk(this)?.homePath
        ?: FlutterSdk.getFlutterSdk(this)?.dartSdkPath