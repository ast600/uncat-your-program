lazy val V = _root_.scalafix.sbt.BuildInfo

inThisBuild(
  List(
    scalaVersion := "2.13.16",
    version := "1.0.1",
    organization := "dev.ast600",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalacOptions += "-Wunused"
  )
)

lazy val zio = "dev.zio" %% "zio" % "2.1.19"
lazy val cats = "org.typelevel" %% "cats-core" % "2.12.0"

lazy val `uncat-your-program` = (project in file("."))
  .aggregate(rules, input, output, tests)
  .settings(
    publish / skip := true
  )

lazy val rules = (project in file("rules"))
  .settings(
    moduleName := "uncat-your-program",
    libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion
  )

lazy val input = (project in file("input"))
  .settings(
    publish / skip := true,
    libraryDependencies ++= Seq(
      "org.typelevel" %% "mouse" % "1.3.2",
      cats,
      zio
    )
  )

lazy val output = (project in file("output"))
  .settings(
    publish / skip := true,
    libraryDependencies ++= Seq(cats, zio)
  )

lazy val tests = (project in file("tests"))
  .settings(
    publish / skip := true,
    scalafixTestkitOutputSourceDirectories := (output / Compile / unmanagedSourceDirectories).value,
    scalafixTestkitInputSourceDirectories := (input / Compile / unmanagedSourceDirectories).value,
    scalafixTestkitInputClasspath := (input / Compile / fullClasspath).value,
    scalafixTestkitInputScalacOptions := (input / Compile / scalacOptions).value,
    scalafixTestkitInputScalaVersion := (input / Compile / scalaVersion).value
  )
  .dependsOn(rules)
  .enablePlugins(ScalafixTestkitPlugin)
