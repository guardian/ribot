package ribot

import java.io.File

import org.joda.time.{DateTimeZone, LocalDate, LocalTime}
import ribot.billing.BillingCsvReader
import ribot.model._
import ribot.reservations.ReservationData

object RibotMain extends App {

  val filename = "/Users/gtackley/billing/362307275615-aws-billing-detailed-line-items-with-resources-and-tags-2014-07.csv.zip"
  //val filename = "/Users/gtackley/billing/smaller.csv.zip"

  val yesterday = new LocalDate().minusDays(1)
  val yesterdayAtEightPm = yesterday.toDateTime(new LocalTime(20, 0), DateTimeZone.UTC)

  val usagesByRegion = BillingCsvReader
    .parseZip(new File(filename))
    .filter(_.isEc2InstanceUsage)
    .filter(_.availabilityZone != null)
    .filter(_.usageStartDate == yesterdayAtEightPm)
    // TODO: need to figure out VPS vs Classic
    .map(_.asUsage(Classic))
    .groupBy(_.region)
    .map { case (region, regionUsages) => UsagesByRegion(region, regionUsages.toList)}

  for (regionUsages <- usagesByRegion if regionUsages.region == "eu-west-1") {
    println(s"*** region: ${regionUsages.region} ***")

    val reservations = ReservationData(regionUsages.region)

    // now by instance class
    for (instanceClass <- InstanceType.classes) {
      val actualUsage = regionUsages.forInstanceClass(instanceClass)
      val desiredReservations = actualUsage.map(_.reservationCriteria)
      val actualReservations = reservations.forClass(instanceClass).all.toList

      if (desiredReservations.size + actualReservations.size > 0) {
        println("\n\nINSTANCE CLASS: " + instanceClass)


        println("usage yesterday: ")
        Usage.prettyPrint(actualUsage)

        val groups = ReservationGroup.make(actualReservations)

        val newGroups = ReservationAllocator.makeItSo(desiredReservations, groups)

        println("After allocation:")
        for (group <- newGroups) {
          println(" * " + group)
          println(group.describeAction)
        }

      }

    }

  }


}


object ReservationAllocator {
  def makeItSo(unallocatedDesiredReservations: List[ReservationCriteria], groups: List[ReservationGroup]): List[ReservationGroup] = {
    val unallocated = ReservationCriteria.unaggregate(unallocatedDesiredReservations)
      .sortBy(_.points).reverse

    unallocated.foldLeft(groups) { case (acc, nextDesiredReservation) =>
      possiblyApply(acc, nextDesiredReservation)
    }
  }

  def possiblyApply(groups: List[ReservationGroup], r: ReservationCriteria): List[ReservationGroup] = {
    def possiblyApplyImpl(nextGroups: List[ReservationGroup]): List[ReservationGroup] = {
      nextGroups match {
        case Nil => Nil
        case head :: rest if head.sparePoints >= r.points => head.spend(r) :: rest
        case head :: rest => head :: possiblyApplyImpl(rest)
      }
    }

    possiblyApplyImpl(groups)
  }

}


