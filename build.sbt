name := "fabrication-engine"

version := "0.0.1"

scalaVersion := "2.13.1"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-language:reflectiveCalls")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.1" % Test
libraryDependencies += "org.scalamock" %% "scalamock" % "4.4.0" % Test
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.9" % Test
libraryDependencies += "org.clapper" %% "grizzled-slf4j" % "1.3.4"

