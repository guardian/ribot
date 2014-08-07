package ribot

import java.io.File

import com.typesafe.config.{ConfigException, ConfigFactory}

object Configuration {
  private lazy val configFile = new File(sys.props("user.home"), ".ribot")

  private lazy val configData = ConfigFactory.parseFile(configFile)

  lazy val billingS3Bucket = getMandatory("s3.bucket")

  private def getMandatory(key: String): String = try {
    configData.getString(key)
  } catch {
    case e: ConfigException =>
      sys.error(s"expecting an entry $key=???? in ${configFile.getCanonicalPath}")
  }
}
