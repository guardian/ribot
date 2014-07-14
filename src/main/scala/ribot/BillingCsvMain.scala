package ribot

import java.io.File

import org.joda.time.{DateTimeZone, LocalTime, LocalDate}
import ribot.billing.BillingCsvReader
import ribot.model.{UsagesByRegion, Classic}

object BillingCsvMain extends App {

  println("hi dude!")

  val filename = "/Users/gtackley/billing/362307275615-aws-billing-detailed-line-items-with-resources-and-tags-2014-07.csv.zip"

  val yesterday = new LocalDate(2014, 7, 14).minusDays(1)
  val yesterdayAtEightPm = yesterday.toDateTime(new LocalTime(20, 0), DateTimeZone.UTC)

  val usages = BillingCsvReader
    .parseZip(new File(filename))
    .filter(_.isEc2InstanceUsage)
    .filter(_.availabilityZone != null)
    .filter(_.usageStartDate == yesterdayAtEightPm)
    // TODO: need to figure out VPS vs Classic
    .map(_.asUsage(Classic))
    .groupBy(_.region)
    .map { case (region, regionUsages) => UsagesByRegion(region, regionUsages.toList) }


  for (regionInfo <- usages) {
    println()
    println(s"*** ${regionInfo.region} ***")
    println()

    for ((instanceClass, rrr) <- regionInfo.reservationsRequired.groupBy(_.instanceType.instanceClass).toList.sortBy(_._1)) {
      println(s"  $instanceClass: required ${rrr.map(r => r.instanceType.sizeNormalistionFactor).sum}")
    }
  }


  /*

  dump detail!
  for (regionInfo <- usages) {
    println()
    println(s"*** ${regionInfo.region} ***")
    println()

    for ( (instanceClass, classUsages) <- regionInfo.usages.groupBy(_.instanceType.instanceClass))  {
      println(s"  class $instanceClass")

      val (reserved, unreserved) = classUsages.partition(_.wasReserved)

      println(s"    ${reserved.size} reserved (hourly cost = $$${reserved.map(_.hourlyCost).sum}")
      reserved.sortBy(_.instanceType.name).foreach(s => println("      " + s))
      println(s"    ${unreserved.size} unreserved (hourly cost = $$${unreserved.map(_.hourlyCost).sum}")
      unreserved.sortBy(_.instanceType.name).foreach(s => println("      " + s))

    }

  }


   */



}
