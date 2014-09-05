package ribot.billing

import java.io.{InputStreamReader, Reader, FileReader, File}
import java.util.zip.ZipFile

import org.supercsv.comment.CommentMatcher
import org.supercsv.io.CsvMapReader
import org.supercsv.prefs.CsvPreference
import ribot.ClassLogger
import scala.collection.convert.decorateAll._

object BillingCsvReader extends ClassLogger {

  def parseZip(zipFile: File) = {


    val zip = new ZipFile(zipFile)
    // expect only a single entry in the zip
    val first = zip.entries().asScala.next()

    log.info("reading " + first)



    parse(new InputStreamReader(zip.getInputStream(first), "US-ASCII"))
  }

  private class CommentEverythingNotEc2Related extends CommentMatcher {
    var isFirstLine = true

    override def isComment(line: String): Boolean = {

      val isInterestingDataLine =
        (line contains "Amazon Elastic Compute Cloud") &&
        (line contains "Usage") &&
        (line contains "RunInstances")

      val result = !isFirstLine && !isInterestingDataLine

      isFirstLine = false

      result
    }

  }

  def parse(reader: Reader): Stream[BillingCsvRow] = {
    // we're only interested in a very small subset of lines which take ages to parse
    // so treat lines that we're not interested in as "comments" to prevent csv parsing

    val csvPrefs = new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE)
      .skipComments(new CommentEverythingNotEc2Related)
      .build()

    val csvReader = new CsvMapReader(reader, csvPrefs)

    val headers = csvReader.getHeader(true)

    def streamUntilEof(): Stream[BillingCsvRow] = {
      val line = csvReader.read(headers: _*)

      if (csvReader.getLineNumber % 100000 == 0)
        log.info(s"[reading line ${csvReader.getLineNumber}]")

      if (line == null) Stream.empty
      else new BillingCsvRow(line.asScala.toMap, csvReader.getLineNumber) #:: streamUntilEof()
    }

    streamUntilEof()
  }
}
