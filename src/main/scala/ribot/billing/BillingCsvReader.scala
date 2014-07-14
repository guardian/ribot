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


//    var interestingRows = List[Row]()
//
//    import scala.util.control.Breaks._
//
//    breakable {
//      val line = reader.read(headers: _*)
//
//      if (line == null)
//        break()
//
//
//      {
//        println("finished!")
//
//        interestingRows.groupBy(r => r.az + " " + r.instanceSize).mapValues(_.length).toList.sortBy(_._1).foreach {
//          case (info, num) => println(s"$num of $info")
//        }
//
//        sys.exit()
//      }
//
//      val scalaLine = line.asScala.toMap
//
//
//
//
//      val lineNumber = reader.getLineNumber
//
//      if (
//        scalaLine("ProductName") == "Amazon Elastic Compute Cloud" &&
//          scalaLine("UsageType").contains("Usage") &&
//          scalaLine("Operation") == "RunInstances" &&
//          scalaLine("UsageQuantity") == "1.00000000" &&
//          scalaLine("UsageStartDate") == "2014-07-05 19:00:00"
//      ) {
//
//        val id = scalaLine("RecordId")
//
//        interestingRows = new Row(scalaLine) :: interestingRows
//      }
//
//      if (lineNumber % 10000 == 0) {
//        println(s"$lineNumber (${interestingRows.size} captured so far)")
//      }

}
