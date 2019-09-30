name := "sales-taxes"

version := "0.1"

scalaVersion := "2.12.8"

resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3")

// if your project uses multiple Scala versions, use this for cross building
addCompilerPlugin(
  "org.typelevel" % "kind-projector" % "0.10.3" cross CrossVersion.binary)

// if your project uses both 2.10 and polymorphic lambdas
cancelable in Global := true
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "org.scalaz" %% "scalaz-core" % "7.2.28",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
) ++ (scalaBinaryVersion.value match {
  case "2.10" =>
    compilerPlugin(
      "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full) :: Nil
  case _ =>
    Nil
})
