package ribot.billing

import java.io.{InputStreamReader, Reader, FileReader, File}
import java.util.zip.ZipFile

import org.supercsv.io.CsvMapReader
import org.supercsv.prefs.CsvPreference
import scala.collection.convert.decorateAll._

object BillingCsvReader {

  def parseZip(zipFile: File) = {
    val zip = new ZipFile(zipFile)
    // expect only a single entry in the zip
    val first = zip.entries().asScala.next()

    println("reading " + first)

    parse(new InputStreamReader(zip.getInputStream(first), "UTF-8"))
  }

  def parse(reader: Reader): Stream[BillingCsvRow] = {
    val csvReader = new CsvMapReader(reader, CsvPreference.STANDARD_PREFERENCE)

    val headers = csvReader.getHeader(true)

    def streamUntilEof(): Stream[BillingCsvRow] = {
      val line = csvReader.read(headers: _*)

      if (csvReader.getLineNumber % 100000 == 0)
        println(s"[reading line ${csvReader.getLineNumber}]")

      if (line == null) Stream()
      else new BillingCsvRow(line.asScala.toMap, csvReader.getLineNumber) #:: streamUntilEof()
    }

    streamUntilEof()
  }
}
