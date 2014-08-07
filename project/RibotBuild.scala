import com.typesafe.sbt.less.Import.LessKeys
import com.typesafe.sbt.web.Import._
import com.typesafe.sbt.web.SbtWeb
import sbt.Keys._
import sbt._

import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._

object RibotBuild extends Build {

  val scalaLibraryVersion = "2.11.2"

  lazy val root = sbt.Project("root", file("."))
    .aggregate(ribot, web)
    .settings(scalaVersion := scalaLibraryVersion)


  val standardSettings = Seq[Setting[_]](
    resolvers ++= Seq(
      "OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/",
      "Typesafe Releases Repository" at "http://repo.typesafe.com/typesafe/releases/"
    ),

    scalaVersion := scalaLibraryVersion,
    scalacOptions := List("-feature", "-deprecation"),

    // Don't include documentation in artifact
    doc in Compile <<= target.map(_ / "none")
  )


  lazy val ribot = sbt.Project("ribot", file("ribot"))
    .settings(standardSettings: _*)
    .settings(
      libraryDependencies ++= Seq(
        "net.sf.supercsv" % "super-csv" % "2.2.0",
        "com.typesafe.play" %% "play-json" % "2.3.2",
        "com.amazonaws" % "aws-java-sdk" % "1.8.7",
        "joda-time" % "joda-time" % "2.4",

        "com.google.guava" % "guava" % "15.0",
        "com.google.code.findbugs" % "annotations" % "3.0.0",
        "org.slf4j" % "slf4j-api" % "1.7.7",

        "org.joda" % "joda-convert" % "1.7" % "provided",
        "org.slf4j" % "slf4j-simple" % "1.7.7" % "test",

        "org.scalatest" %% "scalatest" % "2.2.1" % "test"
      ),

      fork in run := true,
      javaOptions in run += "-Xmx2G"
    )

  lazy val web = Project("web", file("web"))
    .enablePlugins(play.PlayScala)
    .enablePlugins(SbtWeb)
    .dependsOn(ribot)
    .settings(standardSettings: _*)
    .settings(

      LessKeys.compress in Assets := true,

      includeFilter in (Assets, LessKeys.less) := "*.less",

      libraryDependencies ++= Seq(
        "com.google.visualization" % "visualization-datasource" % "1.1.1",

        "org.webjars" % "bootstrap" % "3.2.0",
        "org.webjars" % "jquery" % "2.1.1",
        "org.webjars" % "rickshaw" % "1.5.0"
      ),

      maintainer := "Graham Tackley <graham.tackley@theguardian.com>",

      dockerExposedPorts in Docker := List(9000)

    )


}