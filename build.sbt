import com.typesafe.tools.mima.core._

ThisBuild / tlBaseVersion := "2.1"

ThisBuild / organization := "org.scodec"
ThisBuild / organizationName := "Scodec"

ThisBuild / startYear := Some(2013)

ThisBuild / crossScalaVersions := Seq("3.1.2")

ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin("8"))

ThisBuild / scmInfo := Some(
  ScmInfo(url("https://github.com/scodec/scodec"), "git@github.com:scodec/scodec.git")
)

ThisBuild / licenses := List(
  ("BSD-3-Clause", url("https://github.com/scodec/scodec/blob/main/LICENSE"))
)

ThisBuild / developers ++= List(
  tlGitHubDev("mpilquist", "Michael Pilquist"),
  tlGitHubDev("pchiusano", "Paul Chiusano")
)

ThisBuild / mimaBinaryIssueFilters ++= Seq(
  ProblemFilters.exclude[DirectMissingMethodProblem]("scodec.IsoLowPriority.toTuple"),
  ProblemFilters.exclude[DirectMissingMethodProblem]("scodec.IsoLowPriority.fromTuple"),
  ProblemFilters.exclude[DirectMissingMethodProblem]("scodec.codecs.codecs#package.zlib"),
  ProblemFilters.exclude[DirectMissingMethodProblem]("scodec.codecs.codecs#package.zlib$default$2"),
  ProblemFilters.exclude[DirectMissingMethodProblem]("scodec.codecs.codecs#package.zlib$default$3"),
  ProblemFilters.exclude[DirectMissingMethodProblem]("scodec.codecs.codecs#package.zlib$default$4"),
  ProblemFilters.exclude[DirectMissingMethodProblem]("scodec.codecs.codecs#package.zlib$default$5"),
  ProblemFilters.exclude[DirectMissingMethodProblem]("scodec.codecs.codecs#package.zlib"),
  ProblemFilters.exclude[DirectMissingMethodProblem]("scodec.codecs.codecs#package.zlib$default$2"),
  ProblemFilters.exclude[DirectMissingMethodProblem]("scodec.codecs.codecs#package.zlib$default$3"),
  ProblemFilters.exclude[DirectMissingMethodProblem]("scodec.codecs.codecs#package.zlib$default$4"),
  ProblemFilters.exclude[DirectMissingMethodProblem]("scodec.codecs.codecs#package.zlib$default$5")
)

lazy val root = tlCrossRootProject.aggregate(testkit, core, unitTests, benchmarks)

lazy val core = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .in(file("."))
  .settings(
    name := "scodec-core",
    libraryDependencies ++= Seq(
      "org.scodec" %%% "scodec-bits" % "1.1.32"
    ),
    scalacOptions := scalacOptions.value
      .filterNot(_ == "-source:3.0-migration") :+ "-source:future",
    Test / scalacOptions := (Compile / scalacOptions).value,
    Compile / unmanagedResources ++= {
      val base = baseDirectory.value
      (base / "NOTICE") +: (base / "LICENSE") +: ((base / "licenses") * "LICENSE_*").get
    }
  )
  .jsSettings(
    mimaBinaryIssueFilters ++= Seq(
      ProblemFilters.exclude[MissingClassProblem]("scodec.codecs.ZlibCodec")
    )
  )
  .nativeSettings(
    tlVersionIntroduced := Map("3" -> "2.1.1")
  )

lazy val testkit = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .settings(
    name := "scodec-testkit",
    libraryDependencies += "org.scalameta" %%% "munit-scalacheck" % "1.0.0-M4",
    scalacOptions := scalacOptions.value.filterNot(_ == "-source:3.0-migration") :+ "-source:future"
  )
  .nativeSettings(
    tlVersionIntroduced := Map("3" -> "2.1.1")
  )
  .dependsOn(core)

lazy val unitTests = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .settings(
    scalacOptions := scalacOptions.value.filterNot(
      _ == "-source:3.0-migration"
    ) :+ "-source:future",
    Test / scalacOptions := (Compile / scalacOptions).value
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "org.bouncycastle" % "bcpkix-jdk15on" % "1.70" % Test
    )
  )
  .dependsOn(testkit)
  .enablePlugins(NoPublishPlugin)

lazy val benchmarks = project
  .dependsOn(core.jvm)
  .enablePlugins(JmhPlugin, NoPublishPlugin)
