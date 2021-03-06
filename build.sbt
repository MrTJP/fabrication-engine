// Package details
organization := "io.github.mrtjp"
name := "fabrication-engine"
version := sys.env.getOrElse("GH_ACTIONS_VER", "0.0.0.1") //Set by Github Action

// Compiler settings
scalaVersion := "2.13.1"
scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-language:reflectiveCalls")

// Dependencies
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.1" % Test
libraryDependencies += "org.scalamock" %% "scalamock" % "4.4.0" % Test
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.9" % Test
libraryDependencies += "org.clapper" %% "grizzled-slf4j" % "1.3.4"

// Testing
coverageEnabled := true
coverageHighlighting := true
logBuffered in Test := false

// Publishing
externalResolvers += "GitHub Package Registry" at "https://maven.pkg.github.com/mrtjp/fabrication-engine"
publishTo := Some("GitHub Package Registry" at "https://maven.pkg.github.com/mrtjp/fabrication-engine")
credentials += Credentials("GitHub Package Registry", "maven.pkg.github.com", "mrtjp", sys.env.getOrElse("GITHUB_TOKEN", "password"))

pomIncludeRepository := (_ => false)
publishMavenStyle := true

scmInfo := Some(
    ScmInfo(
        url("https://github.com/mrtjp/fabrication-engine"),
        "scm:git:git@github.com:mrtjp/fabrication-engine.git"
    )
)
developers := List(
    Developer("mrtjp", "mrtjp", "mrtjp@icloud.com", url("http://mrtjp.github.io"))
)
