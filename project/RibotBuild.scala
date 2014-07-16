import com.typesafe.sbt.less.Import.LessKeys
import com.typesafe.sbt.web.Import._
import com.typesafe.sbt.web.SbtWeb
import sbt.Keys._
import sbt._

object RibotBuild extends Build {

  val scalaLibraryVersion = "2.11.1"

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
        "org.elasticsearch" % "elasticsearch" % "1.2.1",
        "com.typesafe.play" %% "play-json" % "2.3.1",
        "com.amazonaws" % "aws-java-sdk" % "1.8.4",
        "joda-time" % "joda-time" % "2.3",

        "org.joda" % "joda-convert" % "1.6" % "provided",

        "org.scalatest" %% "scalatest" % "2.2.0" % "test"
      )
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
        "org.webjars" % "bootstrap" % "3.2.0",
        "org.webjars" % "jquery" % "2.1.1"
      )

    )


}