import Dependencies._
import Settings._

lazy val alam = project.in(file("."))
  .enablePlugins(NativeImagePlugin)
  .settings(
    name := "awslambda",
    organization := "de.rmgk",
    scalaVersion_213,
    strictCompile,
    libraryDependencies ++= Seq(
      decline.value,
      betterFiles.value,
      scalatest.value,
      scalacheck.value,
      scribe.value,
    ),
    libraryDependencies ++= jsoniterScalaAll.value,
    nativeImageVersion := "20.3.0",
    nativeImageOptions ++= Seq(
      "--initialize-at-build-time",
      "--no-fallback",
      "--no-server",
      "-H:EnableURLProtocols=http,https",
    ),
  )

// fix some linting nonsene
Global / excludeLintKeys += nativeImageVersion