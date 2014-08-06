package ribot

import org.slf4j.LoggerFactory


trait ClassLogger {
  protected lazy val log = LoggerFactory.getLogger(getClass)


  protected final def logAround[T](msg: String)(block: => T) = {
    log.info(s"$msg...")
    val result = block
    log.info(s"$msg complete")
    result
  }

}