name := "leekismg"

version := "1.0"

val supportLibsVersion = "25.2.0"

lazy val root = project.in(file("."))

val fullOptAndroid = Def.taskKey[File]("Generate the file given to react native")

lazy val mobile = project.in(file("mobile")).
  enablePlugins(ScalaJSPlugin)
  .settings(
    scalaVersion := "2.12.1",
    libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % "1.0.0-RC2",
    scalaJSUseMainModuleInitializer := true,
    scalaJSModuleKind := ModuleKind.CommonJSModule,
    fullOptAndroid in Compile := {
    val outFile = (root.base / "index.android.js")
    IO.copyFile((fullOptJS in Compile).value.data, outFile)
    outFile
  })

lazy val androidLauncher =  project.in(file("platform/android")).
  enablePlugins(AndroidApp).
  settings(
    scalaVersion := "2.11.9",
    android.useSupportVectors,
    versionCode := Some(1),
    version := "0.1-SNAPSHOT",
    instrumentTestRunner := "android.support.test.runner.AndroidJUnitRunner",
    dexMaxHeap := "4g",
    organization := "org.codeck",
    platformTarget := "android-25",
    minSdkVersion := "16",
    compileOrder := CompileOrder.JavaThenScala,
    javacOptions in Compile ++= "-source" :: "1.7" :: "-target" :: "1.7" :: Nil,
    scalacOptions in Compile ++= "-target:jvm-1.7" :: "-Xexperimental" :: Nil,
    ndkArgs := "-j" :: java.lang.Runtime.getRuntime.availableProcessors.toString :: Nil,
    resolvers += "Local react-native Repository" at (root.base / "node_modules" / "react-native" / "android").toURI.toString,
    libraryDependencies ++=
      "com.android.support" % "multidex" % "1.0.1" ::
      "com.android.support" % "appcompat-v7" % supportLibsVersion ::
        "com.android.support.test" % "runner" % "0.5" % "androidTest" ::
        "com.android.support.test.espresso" % "espresso-core" % "2.2.2" % "androidTest" ::
        "com.facebook.react" % "react-native" % "0.43.1" ::
        Nil,
    useProguard := true,
    proguardScala := true,
    dexMulti := true,
    //proguardConfig -= "-dontobfuscate",
    //proguardConfig -= "-dontoptimize",
    ////rules from https://github.com/facebook/react-native/blob/master/local-cli/templates/HelloWorld/android/app/proguard-rules.pro
    proguardOptions ++= Seq(
      //for react native
      "-keep,allowobfuscation @interface com.facebook.proguard.annotations.DoNotStrip",
      "-keep,allowobfuscation @interface com.facebook.proguard.annotations.KeepGettersAndSetters",
      "-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip",
      "-keep @com.facebook.proguard.annotations.DoNotStrip class *",
      "-keep @com.facebook.common.internal.DoNotStrip class *",
      """-keepclassmembers class * { @com.facebook.proguard.annotations.DoNotStrip *; @com.facebook.common.internal.DoNotStrip *; }""",
      """-keepclassmembers @com.facebook.proguard.annotations.KeepGettersAndSetters class * { void set*(***); *** get*(); }""",
      "-keep class * extends com.facebook.react.bridge.JavaScriptModule { *; }",
      "-keep class * extends com.facebook.react.bridge.NativeModule { *; }",
      "-keepclassmembers,includedescriptorclasses class * { native <methods>; }",
      "-keepclassmembers class *  { @com.facebook.react.uimanager.UIProp <fields>; }",
      "-keepclassmembers class *  { @com.facebook.react.uimanager.annotations.ReactProp <methods>; }",
      "-keepclassmembers class *  { @com.facebook.react.uimanager.annotations.ReactPropGroup <methods>; }",
      "-keep class com.facebook.** { *; }",
      "-dontwarn com.facebook.react.**",
      "-dontwarn android.text.StaticLayout",
      "-keepattributes Signature",
      "-keepattributes *Annotation*",
      "-keep class okhttp3.** { *; }",
      "-keep interface okhttp3.** { *; }",
      "-dontwarn okhttp3.**",
      "-keep class sun.misc.Unsafe { *; }",
      "-dontwarn java.nio.file.*",
      "-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement",
      "-dontwarn okio.**",
      //patch for stellar sdk
      "-dontwarn org.mockito.**",
      "-dontwarn org.apache.commons.logging.**",
      "-dontwarn com.google.**",
      "-dontwarn net.bytebuddy.**",
      "-dontwarn org.glassfish.**",
      "-dontwarn com.sun.research.ws.wadl.**",
      "-dontwarn jersey.repackaged.com.google.**",
      "-dontwarn javassist.**",
      "-dontwarn javax.ws.rs.core.**",
      "-dontwarn org.apache.http.**",
      "-dontwarn org.objenesis.instantiator.**",
      ""
    )
  )