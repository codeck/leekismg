name := "leekismg"

version := "1.0"

scalaVersion := "2.11.9"

val supportLibsVersion = "25.2.0"

lazy val mobile = project.in(file("mobile")).
  enablePlugins(ScalaJSPlugin)

lazy val androidLauncher =  project.in(file("platform/android")).
  enablePlugins(AndroidApp).
  settings(
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
    libraryDependencies ++=
      "com.android.support" % "appcompat-v7" % supportLibsVersion ::
        "com.android.support.test" % "runner" % "0.5" % "androidTest" ::
        "com.android.support.test.espresso" % "espresso-core" % "2.2.2" % "androidTest" ::
        Nil,
    useProguard := true,
    proguardScala := true,
    //proguardConfig -= "-dontobfuscate",
    //proguardConfig -= "-dontoptimize",
    proguardOptions ++= Seq("-keep class com.foo.bar.Baz")
  )
