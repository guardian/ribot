scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "net.sf.supercsv" % "super-csv" % "2.2.0",
  "org.elasticsearch" % "elasticsearch" % "1.2.1",
  "com.typesafe.play" %% "play-json" % "2.3.1",
  "com.amazonaws" % "aws-java-sdk" % "1.8.4",
  "org.scalatest" %% "scalatest" % "2.2.0" % "test",
  "org.joda" % "joda-convert" % "1.6" % "provided",
  "joda-time" % "joda-time" % "2.3"
)
