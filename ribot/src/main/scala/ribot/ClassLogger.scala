package ribot

import org.slf4j.LoggerFactory

import com.google.common.base.Stopwatch

trait ClassLogger {
  protected lazy val log = LoggerFactory.getLogger(getClass)


  protected final def logAround[T](msg: String)(block: => T) = {
    log.info(s"$msg...")
    val sw = Stopwatch.createStarted()
    val result = block
    log.info(s"$msg complete in $sw")
    result
  }

}