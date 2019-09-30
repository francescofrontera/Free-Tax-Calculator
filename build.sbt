name := "sales-taxes"

version := "0.1"

scalaVersion := "2.12.8"

resolvers += Resolver.sonatypeRepo("releases")

// if your project uses both 2.10 and polymorphic lambdas
cancelable in Global := true

lazy val scalazVersion = "7.2.28"
lazy val scalaTestVersion = "3.0.5"

scalacOptions := Seq(
  "-unchecked",
  "-deprecation",
  "-target:jvm-1.8",
  "-feature",
  "-language:implicitConversions",
  "-language:higherKinds"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
  "org.scalaz" %% "scalaz-effect" % scalazVersion,
  "org.scalaz" %% "scalaz-core" % scalazVersion,
)
