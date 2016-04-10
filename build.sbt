name := "funsandbox"

version := "1.0"

scalaVersion := "2.11.8"

val scalazVersion = "7.1.0"
val scalatestVersion = "2.2.4"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % scalazVersion,
  "org.scalaz" %% "scalaz-effect" % scalazVersion,
  "org.scalaz" %% "scalaz-typelevel" % scalazVersion,
  "org.scalatest" %% "scalatest" % scalatestVersion % "test",
  "org.scalaz" %% "scalaz-scalacheck-binding" % scalazVersion % "test"
)
    