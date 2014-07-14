package ribot

import java.io.File

import org.joda.time.{DateTimeZone, LocalTime, LocalDate}
import ribot.billing.BillingCsvReader
import ribot.model.Classic

object BillingCsvMain extends App {

  println("hi dude!")

  val filename = "/Users/gtackley/billing/362307275615-aws-billing-detailed-line-items-with-resources-and-tags-2014-06.csv.zip"

  val yesterday = new LocalDate().minusDays(1)
  val yesterdayAtEightPm = yesterday.toDateTime(new LocalTime(20, 0), DateTimeZone.UTC)

  val usages = BillingCsvReader
    .parseZip(new File(filename))
    .filter(_.isEc2InstanceUsage)
    .filter(_.availabilityZone == null)

//    .filter(_.isActuallyAnInstanceUsage)
//    .filter(_.usageStartDate == yesterdayAtEightPm)
//    // TODO: need to figure out VPS vs Classic
//    .map(_.asUsage(Classic))

  usages foreach { u =>
    println(s"${u.itemDescription} = at a cost of: ${u.unblendedCost}")
    println()
  }


//    .take(10)
//    .foreach { r =>
//    println(r)
//
//    println(s"start = ${r.usageStartDate} -> ${r.usageEndDate}, ri? ${r.reservedInstance} blendedRate = ${r.blendedRate}")
//  }

  private val sum = usages.map(_.unblendedCost).sum
  println("done - found " + usages.size + " with a total cost of $" + sum)

}
